package kr.co.teacherforboss.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class MailConfig {
    @Value("${spring.mail.username}")
    private String username;
    private final String name = "teacherforboss";
}
