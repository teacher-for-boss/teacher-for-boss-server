package kr.co.teacherforboss.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Getter
@Configuration
public class AwsSnsConfig {

    @Value("${cloud.aws.sns.credentials.access-key}")
    private String awsAccessKey;

    @Value("${cloud.aws.sns.credentials.secret-key}")
    private String awsSecretKey;

    @Value("${cloud.aws.region}")
    private String awsRegion;

    @Value("${cloud.aws.sns.topic-arn-for-all}")
    private String snsTopicARNForAll;
    @Value("${cloud.aws.sns.topic-arn-for-general}")
    private String snsTopicARNForGeneral;
    @Value("${cloud.aws.sns.topic-arn-for-specific}")
    private String snsTopicARNForSpecific;
    @Value("${cloud.aws.sns.platform-application-arn}")
    private String snsPlatformApplicationARN;

    public static String SNS_TOPIC_ARN_FOR_ALL;
    public static String SNS_TOPIC_ARN_FOR_GENERAL;
    public static String SNS_TOPIC_ARN_FOR_SPECIFIC;
    public static String SNS_PLATFORM_APPLICATION_ARN;

    @PostConstruct
    public void init() {
        SNS_TOPIC_ARN_FOR_ALL = snsTopicARNForAll;
        SNS_TOPIC_ARN_FOR_GENERAL = snsTopicARNForGeneral;
        SNS_TOPIC_ARN_FOR_SPECIFIC = snsTopicARNForSpecific;
        SNS_PLATFORM_APPLICATION_ARN = snsPlatformApplicationARN;
    }


    @Bean
    public SnsClient getSnsClient() {
        return SnsClient.builder()
                .region(Region.of(awsRegion)) // 리전 설정 추가
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .build();
    }

}
