package kr.co.teacherforboss.service.mailService;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.MailHandler;
import kr.co.teacherforboss.config.MailConfig;
import kr.co.teacherforboss.domain.vo.mailVO.Mail;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class MailCommandServiceImpl implements MailCommandService {

    private final MailConfig mailConfig;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;
    private Mail mail;

    @Override
    public String sendMail(String to, Mail mail) {
        try {
            this.mail = mail;
            MimeMessage message = createMessage(to);
            javaMailSender.send(message);
        } catch (MailException | MessagingException | UnsupportedEncodingException es) {
            throw new MailHandler(ErrorStatus.MAIL_SEND_FAIL);
        }
        return to;
    }

    private MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {
        final String from = mailConfig.getUsername();
        final String fromName = mailConfig.getName();

        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(RecipientType.TO, to);
        message.setSubject(mail.getTitle());
        message.setText(setContext(), "utf-8", "html");
        message.setFrom(new InternetAddress(from, fromName));

        return message;
    }

    private String setContext() {
        Context context = new Context();
        Map<String, String> values = mail.getValues();

        if (!values.isEmpty()) {
            for (String key : values.keySet()) {
                context.setVariable(key, values.get(key));
            }
        }

        return springTemplateEngine.process(mail.getFilename(), context);
    }

}
