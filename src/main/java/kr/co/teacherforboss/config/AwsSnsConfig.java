package kr.co.teacherforboss.config;

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


    @Bean
    public SnsClient getSnsClient() {
        return SnsClient.builder()
                .region(Region.of(awsRegion)) // 리전 설정 추가
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .build();
    }

}
