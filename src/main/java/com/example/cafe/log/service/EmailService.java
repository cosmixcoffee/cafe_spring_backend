package com.example.cafe.log.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;



@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}") // 환경변수에서 발신자 이메일 가져오기
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendTemporaryPassword(String toEmail, String tempPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("CAFETITLE 임시 비밀번호 안내");

            // HTML 지원 (임시 비밀번호 포함)
            String emailContent = "<h3>안녕하세요. CAFETITLE입니다.</h3>"
                    + "<p>회원님의 임시 비밀번호는 <b>" + tempPassword + "</b> 입니다.</p>"
                    + "<p>로그인 후 비밀번호를 변경해 주세요.</p>";
            helper.setText(emailContent, true); // HTML 형식으로 전송

            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
