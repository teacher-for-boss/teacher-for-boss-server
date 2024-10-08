package kr.co.teacherforboss.service.snsService;

import java.time.LocalDateTime;
import java.util.List;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.domain.DeviceToken;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Notification;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.domain.vo.notificationVO.NotificationMessage;
import kr.co.teacherforboss.repository.DeviceTokenRepository;
import kr.co.teacherforboss.repository.NotificationRepository;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreatePlatformEndpointResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnsServiceImpl implements SnsService {

    private final SnsClient snsClient;
    @Value("${cloud.aws.sns.topic-arn-for-all}")
    private String snsTopicARNForAll;
    @Value("${cloud.aws.sns.topic-arn-for-specific}")
    private String snsTopicARNForSpecific;
    @Value("${cloud.aws.sns.platform-application-arn}")
    private String snsPlatformApplicationARN;

    private final NotificationRepository notificationRepository;

    private final DeviceTokenRepository deviceTokenRepository;

    @Transactional
    public void createEndpoint(Member member, AuthRequestDTO.DeviceInfoDTO deviceInfoDTO) {
        System.out.println("Creating platform endpoint with token " + member.getEmail() + " :: " + deviceInfoDTO.getFcmToken());
        try {

            DeviceToken deviceToken = deviceTokenRepository.findByMemberIdAndFcmToken(member.getId(), deviceInfoDTO.getFcmToken())
                    .orElseGet(() -> {
                        DeviceToken newDeviceToken = deviceTokenRepository.save(
                                DeviceToken.builder()
                                        .member(member)
                                        .fcmToken(deviceInfoDTO.getFcmToken())
                                        .platform(deviceInfoDTO.getPlatform())
                                        .build());
                        newDeviceToken.softDelete();
                        return newDeviceToken;
                    });

            if (deviceToken.getStatus().equals(Status.ACTIVE)) return;

            CreatePlatformEndpointResponse response = snsClient.createPlatformEndpoint(r -> r
                    .platformApplicationArn(snsPlatformApplicationARN)
                    .token(deviceInfoDTO.getFcmToken()));

            String subscriptionArn = snsClient.subscribe(r -> r
                    .topicArn(snsTopicARNForAll)
                    .protocol("application")
                    .endpoint(response.endpointArn()))
                    .subscriptionArn();

            deviceToken.revertSoftDelete();
            deviceToken.updateEndpointArn(response.endpointArn());
            deviceToken.updateSubscriptionArn(subscriptionArn);

            System.out.println("The ARN of the endpoint is " + response.endpointArn());
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    @Transactional
    public void deleteEndpoint(Member member) {
        System.out.println("Deleting platform endpoint of " + member.getEmail());
        try {
            List<DeviceToken> deviceTokens = deviceTokenRepository.findAllByMemberId(member.getId());

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

    @Transactional
    public void deleteEndpoint(Member member, String fcmToken) {
        System.out.println("Deleting platform endpoint of " + member.getEmail() + " :: " + fcmToken);
        try {
            DeviceToken deviceToken = deviceTokenRepository.findByMemberIdAndFcmToken(member.getId(), fcmToken)
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

    public void publishMessage(String message) {
        System.out.println("Publishing message to all");
        try {
            snsClient.publish(r -> r
                    .topicArn(snsTopicARNForAll)
                    .message(message));
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    @Async("asyncTaskExecutor")
    public void publishMessage(String message, List<Member> targetMembers) {
        System.out.println("Publishing message to " + targetMembers.toString());
        try {
            List<DeviceToken> deviceTokens = deviceTokenRepository.findAllByMemberIdIn(
                    targetMembers.stream().map(Member::getId).toList());
            if (deviceTokens.isEmpty()) return;

            List<String> subscriptionArns = deviceTokens.stream()
                    .map(deviceToken -> snsClient.subscribe(r -> r
                            .topicArn(snsTopicARNForSpecific)
                            .protocol("application")
                            .endpoint(deviceToken.getEndpointArn())
                    ).subscriptionArn())
                    .toList();

            snsClient.publish(r -> r
                    .topicArn(snsTopicARNForSpecific)
                    .message(message));

            subscriptionArns.forEach(subscriptionArn ->
                    snsClient.unsubscribe(r -> r
                            .subscriptionArn(subscriptionArn))
            );

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    @Async("asyncTaskExecutor")
    public void publishMessage(List<Notification> notifications) {
        notifications.forEach(notification -> {
            System.out.println("Publishing message to " + notification.getMember().getEmail());
            try {
                List<DeviceToken> deviceTokens = deviceTokenRepository.findAllByMemberId(notification.getMember().getId());
                if (deviceTokens.isEmpty()) return;

                List<String> subscriptionArns = deviceTokens.stream()
                        .map(deviceToken -> snsClient.subscribe(r -> r
                                .topicArn(snsTopicARNForSpecific)
                                .protocol("application")
                                .endpoint(deviceToken.getEndpointArn())
                        ).subscriptionArn())
                        .toList();


                snsClient.publish(r -> r
                        .topicArn(snsTopicARNForSpecific)
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
