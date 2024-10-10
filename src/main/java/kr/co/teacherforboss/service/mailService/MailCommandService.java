package kr.co.teacherforboss.service.mailService;

import kr.co.teacherforboss.domain.vo.mailVO.Mail;

public interface MailCommandService {

    void sendMail(String to, Mail mail);
}
