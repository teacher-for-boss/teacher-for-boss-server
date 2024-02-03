package kr.co.teacherforboss.util;

import jakarta.annotation.PostConstruct;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.domain.vo.smsVO.SMS;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoBadRequestException;
import net.nurigo.sdk.message.exception.NurigoInvalidApiKeyException;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsUtil {

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecretKey;

    @Value("${coolsms.domain}")
    private String domain;

    @Value("${coolsms.from-number}")
    private String fromNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, domain);
        fromNumber = fromNumber.replaceAll("-", "");
    }

    public SingleMessageSentResponse sendOne(String to, SMS sms) {
        try {
            Message message = new Message();
            message.setFrom(fromNumber);
            message.setTo(to);
            message.setText(sms.getText());

            return this.messageService.sendOne(new SingleMessageSendingRequest(message));
        } catch (Exception e) {
            log.info("[SMS 전송 실패] " + e);
            throw new AuthHandler(ErrorStatus.SMS_SEND_FAIL);
        }
    }
}
