package kr.co.teacherforboss.domain.enums;

import kr.co.teacherforboss.config.AwsSnsConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationTopic {
    ALL("기본 토픽"),
    GENERAL("일반 토픽"),
    SPECIFIC("특정 토픽");

    private final String description;

    public static NotificationTopic from(String subscriptionARN) {
        if (subscriptionARN.contains(AwsSnsConfig.SNS_TOPIC_ARN_FOR_GENERAL)) {
            return GENERAL;
        }
        if (subscriptionARN.contains(AwsSnsConfig.SNS_TOPIC_ARN_FOR_SPECIFIC)) {
            return SPECIFIC;
        }
        if (subscriptionARN.contains(AwsSnsConfig.SNS_TOPIC_ARN_FOR_ALL)) {
            return ALL;
        }
        return null;
    }

    public static String toARN(NotificationTopic topic) {
        switch (topic) {
            case ALL:
                return AwsSnsConfig.SNS_TOPIC_ARN_FOR_ALL;
            case GENERAL:
                return AwsSnsConfig.SNS_TOPIC_ARN_FOR_GENERAL;
            case SPECIFIC:
                return AwsSnsConfig.SNS_TOPIC_ARN_FOR_SPECIFIC;
            default:
                return null;
        }
    }
}
