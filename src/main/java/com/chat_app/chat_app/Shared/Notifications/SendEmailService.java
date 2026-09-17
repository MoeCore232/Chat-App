package com.chat_app.chat_app.Shared.Notifications;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SendEmailService {

    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender javaMailSender;

    public SendEmailService (JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    public void welcomeMessage(String to) {
        SimpleMailMessage createMessage = new SimpleMailMessage();

        createMessage.setFrom(from);
        createMessage.setTo(to);
        createMessage.setSubject("مرحبًا بك في إشارة");
        createMessage.setText(
                """
                مرحبًا بك في إشارة 👋
    
                سعداء بعودتك إلينا!
    
                أصبح حسابك الآن جاهزًا.
                يمكنك الآن التواصل مع أصدقائك وإرسال الرسائل.
    
                نتمنى لك تجربة جميلة.
    
                -- فريق إشارة --
                """
        );

        javaMailSender.send(createMessage);
    }

    public void confirmationMessage(String to, String code) {
        SimpleMailMessage createMessage = new SimpleMailMessage();

        createMessage.setFrom(from);
        createMessage.setTo(to);
        createMessage.setSubject("تأكيد بريدك الإلكتروني");
        createMessage.setText(
                """
                مرحبًا بك في إشارة!
    
                لإكمال إنشاء حسابك، استخدم رمز التأكيد التالي:
    
                %s
    
                هذا الرمز صالح لمدة 10 دقائق.
    
                إذا لم تقم بإنشاء هذا الحساب، يمكنك تجاهل هذه الرسالة.
    
                -- فريق إشارة --
                """.formatted(code)
        );

        javaMailSender.send(createMessage);
    }
}



