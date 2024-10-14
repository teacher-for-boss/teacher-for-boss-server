package kr.co.teacherforboss.service.snsService;

import java.time.LocalDateTime;
import java.util.List;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.config.AwsSnsConfig;
import kr.co.teacherforboss.domain.DeviceToken;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Notification;
import kr.co.teacherforboss.domain.enums.NotificationTopic;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.domain.vo.notificationVO.NotificationMessage;
import kr.co.teacherforboss.repository.DeviceTokenRepository;
import kr.co.teacherforboss.repository.NotificationRepository;
import kr.co.teacherforboss.service.deviceTokenService.DeviceTokenCommandService;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import kr.co.teacherforboss.web.dto.AuthRequestDTO.DeviceInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreatePlatformEndpointResponse;
import software.amazon.awssdk.services.sns.model.NotFoundException;
import software.amazon.awssdk.services.sns.model.SnsException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnsServiceImpl implements SnsService {

    private final SnsClient snsClient;

    private final NotificationRepository notificationRepository;

    private final DeviceTokenRepository deviceTokenRepository;

    private final DeviceTokenCommandService deviceTokenCommandService;

    @Async("asyncTaskExecutor")
    @Transactional
    public void createEndpoint(Member member, List<AuthRequestDTO.DeviceInfoDTO> deviceInfoDTOs, NotificationTopic topic) {
        System.out.println("Creating platform endpoint with token " + member.getEmail());
        deviceInfoDTOs.forEach(deviceInfoDTO -> {
            try {
                String topicARN = NotificationTopic.toARN(topic);
                DeviceToken deviceToken = deviceTokenRepository.findByMemberIdAndFcmTokenAndTopic(member.getId(), deviceInfoDTO.getFcmToken(),
                        NotificationTopic.from(topicARN)).
                        orElseGet(() -> deviceTokenCommandService.saveDeviceToken(member, deviceInfoDTO, NotificationTopic.from(topicARN)));

                if (deviceToken.getStatus().equals(Status.ACTIVE)) return;

                CreatePlatformEndpointResponse response = snsClient.createPlatformEndpoint(r -> r
                        .platformApplicationArn(AwsSnsConfig.SNS_PLATFORM_APPLICATION_ARN)
                        .token(deviceToken.getFcmToken()));

                String subscriptionArn = snsClient.subscribe(r -> r
                                .topicArn(topicARN)
                                .protocol("application")
                                .endpoint(response.endpointArn()))
                        .subscriptionArn();

                deviceToken.revertSoftDelete();
                deviceToken.updateEndpointArn(response.endpointArn());
                deviceToken.updateSubscriptionArn(subscriptionArn);

                System.out.println("The ARN of the endpoint is " + response.endpointArn());
            } catch (SnsException e) {
                System.err.println(e);
            }
        });
    }

    @Async("asyncTaskExecutor")
    @Transactional
    public void deleteEndpoint(Member member) {
        System.out.println("Deleting platform endpoint of " + member.getEmail());
        try {
            List<DeviceToken> deviceTokens = deviceTokenRepository.findAllByMemberIdAndStatus(member.getId(), Status.ACTIVE);

            for (DeviceToken deviceToken : deviceTokens) {

                snsClient.unsubscribe(r -> r
                        .subscriptionArn(deviceToken.getSubscriptionArn()));

                snsClient.deleteEndpoint(r -> r
                        .endpointArn(deviceToken.getEndpointArn()));
                deviceToken.softDelete();
            }
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    @Async("asyncTaskExecutor")
    @Transactional
    public void deleteEndpoint(Member member, List<String> fcmTokens, NotificationTopic topic) {
        System.out.println("Deleting platform endpoint of " + member.getEmail());

        List<DeviceToken> deviceTokens = deviceTokenRepository.findAllByMemberIdAndFcmTokenInAndTopic(member.getId(), fcmTokens, topic);

        for (DeviceToken deviceToken : deviceTokens) {
            try {
                snsClient.unsubscribe(r -> r
                        .subscriptionArn(deviceToken.getSubscriptionArn()));

                snsClient.deleteEndpoint(r -> r
                        .endpointArn(deviceToken.getEndpointArn()));
                deviceToken.softDelete();
            } catch (SnsException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
            }
        }
    }


    @Transactional
    public void deleteEndpoint(Member member, String fcmToken, NotificationTopic topic) {
        System.out.println("Deleting platform endpoint of " + member.getEmail() + " :: " + fcmToken);
        try {
            DeviceToken deviceToken = deviceTokenRepository.findByMemberIdAndFcmTokenAndTopic(member.getId(), fcmToken, topic)
                    .orElseThrow(() -> new AuthHandler(ErrorStatus.LOGIN_INFO_NOT_FOUND));

            snsClient.unsubscribe(r -> r
                    .subscriptionArn(deviceToken.getSubscriptionArn()));

            snsClient.deleteEndpoint(r -> r
                    .endpointArn(deviceToken.getEndpointArn()));

            deviceToken.softDelete();
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    // publish message for all
    public void publishMessage(String message) {
        System.out.println("Publishing message to all");
        try {
            snsClient.publish(r -> r
                    .topicArn(AwsSnsConfig.SNS_TOPIC_ARN_FOR_ALL)
                    .message(message));
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    // publish message for general (members that are agreed to receive general notifications)
    public void publishMessage(Notification notification) {
        System.out.println(notification.getType().name() + " :: Publishing message to general");
        try {
            snsClient.publish(r -> r
                    .topicArn(AwsSnsConfig.SNS_TOPIC_ARN_FOR_GENERAL)
                    .message(NotificationMessage.from(notification).getMessage()));
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    // publish message for specific members
    @Async("asyncTaskExecutor")
    public void publishMessage(String message, List<Member> targetMembers) {
        System.out.println("Publishing message to " + targetMembers.toString());
        try {
            List<DeviceToken> deviceTokens = deviceTokenRepository.findAllByMemberIdInAndStatus(
                    targetMembers.stream().map(Member::getId).toList(), Status.ACTIVE);
            if (deviceTokens.isEmpty()) return;

            List<String> subscriptionArns = deviceTokens.stream()
                    .map(deviceToken -> snsClient.subscribe(r -> r
                            .topicArn(AwsSnsConfig.SNS_TOPIC_ARN_FOR_SPECIFIC)
                            .protocol("application")
                            .endpoint(deviceToken.getEndpointArn())
                    ).subscriptionArn())
                    .toList();

            snsClient.publish(r -> r
                    .topicArn(AwsSnsConfig.SNS_TOPIC_ARN_FOR_SPECIFIC)
                    .message(message));

            subscriptionArns.forEach(subscriptionArn ->
                    snsClient.unsubscribe(r -> r
                            .subscriptionArn(subscriptionArn))
            );

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    // public message for specific members
    @Async("asyncTaskExecutor")
    public void publishMessage(List<Notification> notifications) {
        notifications.forEach(notification -> {
            System.out.println(notification.getType().name() + " :: Publishing message to " + notification.getMember().getEmail());
            try {
                List<DeviceToken> deviceTokens = deviceTokenRepository.findAllByMemberIdAndStatus(
                        notification.getMember().getId(), Status.ACTIVE);
                if (deviceTokens.isEmpty()) return;

                List<String> subscriptionArns = deviceTokens.stream()
                        .map(deviceToken -> snsClient.subscribe(r -> r
                                .topicArn(AwsSnsConfig.SNS_TOPIC_ARN_FOR_SPECIFIC)
                                .protocol("application")
                                .endpoint(deviceToken.getEndpointArn())
                        ).subscriptionArn())
                        .toList();

                snsClient.publish(r -> r
                        .topicArn(AwsSnsConfig.SNS_TOPIC_ARN_FOR_SPECIFIC)
                        .message(NotificationMessage.from(notification).getMessage()));

                notification.setSentAt(LocalDateTime.now());

                subscriptionArns.forEach(subscriptionArn ->
                        snsClient.unsubscribe(r -> r
                            .subscriptionArn(subscriptionArn))
                );

            } catch (SnsException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
            }
        });

        notificationRepository.saveAll(notifications);
    }

}
