package kr.co.teacherforboss.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class S3Config {
	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;

	public static String REGION;

	public static String BUCKET_NAME;

	@Value("${cloud.aws.region}")
	public void setRegion(String region) {
		S3Config.REGION = region;
	}

	@Value("${cloud.aws.s3.bucket}")
	public void setBucket(String bucket) {
		S3Config.BUCKET_NAME = bucket;
	}

	@Bean
	@Primary
	public BasicAWSCredentials awsCredentialsProvider() {
		BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);
		return basicAWSCredentials;
	}

	@Bean
	public AmazonS3 amazonS3() {
		return AmazonS3ClientBuilder.standard()
				.withRegion(REGION)
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentialsProvider()))
				.build();
	}
}
