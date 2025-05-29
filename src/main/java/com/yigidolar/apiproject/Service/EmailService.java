package com.yigidolar.apiproject.Service;

import com.yigidolar.apiproject.Entity.User;
import com.yigidolar.apiproject.Entity.VerifyModel;
import com.yigidolar.apiproject.Repository.VerifyRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Optional;

@Service
public class EmailService {

    private final VerifyRepository verifyRepository;

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    public EmailService(VerifyRepository verifyRepository, JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.verifyRepository = verifyRepository;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerifyEmail(String email,String username, long userID) throws MessagingException {
        VerifyModel verifyModel = new VerifyModel();
        String verifyCode = generateVerificationCode();
        verifyModel.setEmail(email);
        verifyModel.setCode(verifyCode);
        verifyModel.setUserID(userID);
        verifyModel.setType(1);
        verifyRepository.save(verifyModel);

        Context context = new Context();
        context.setVariable("fullName", username);
        context.setVariable("verificationCode", verifyCode);

        String htmlContent = templateEngine.process("verify-email", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("Email Doğrulama Kodu");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public boolean verifyEmail(String email, String code) {
        Optional<VerifyModel> verifyModel = verifyRepository.findByEmailAndType(email, 1);
        if (verifyModel.isPresent()) {
            VerifyModel verify = verifyModel.get();
            if (verify.getCode().equals(code)) {
                verifyRepository.delete(verify);
                return true;
            }
            return false;
        }
        return false;
    }

    public void sendForgetPasswordCode(String email,String username, long userID) throws MessagingException {
        VerifyModel verifyModel = new VerifyModel();
        String verifyCode = generateVerificationCode();
        verifyModel.setEmail(email);
        verifyModel.setCode(verifyCode);
        verifyModel.setUserID(userID);
        verifyModel.setType(2);
        verifyRepository.save(verifyModel);

        Context context = new Context();
        context.setVariable("fullName", username);
        context.setVariable("code", verifyCode);

        String htmlContent = templateEngine.process("forgot-password", context);

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("Şifre Sıfırlama Talebi");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String email, String username, String code) throws MessagingException {
        Context context = new Context();
        context.setVariable("fullName", username);
        context.setVariable("code", code);

        String htmlContent = templateEngine.process("forgot-password", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("Şifre Sıfırlama Bağlantısı");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String generateVerificationCode() {
        int code = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}
