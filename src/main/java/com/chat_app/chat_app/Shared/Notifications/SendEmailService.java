package com.chat_app.chat_app.Shared.Notifications;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class SendEmailService {

    @Value("${sendlib.api-key}")
    private String apiKey;

    @Value("${sendlib.from}")
    private String from;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://sendlib.samueltuoyo.com")
            .build();

    public void welcomeMessage(String to) {

        String html = """
                <h2>مرحبًا بك في إشارة 👋</h2>

                <p>سعداء بانضمامك إلينا!</p>

                <p>
                أصبح حسابك الآن جاهزًا.
                يمكنك الآن التواصل مع أصدقائك وإرسال الرسائل.
                </p>

                <p>نتمنى لك تجربة جميلة.</p>

                <p>-- فريق إشارة --</p>
                """;

        sendEmail(
                to,
                "مرحبًا بك في إشارة",
                html
        );
    }

    public void confirmationMessage(String to, String code) {

        String html = """
                <h2>مرحبًا بك في إشارة!</h2>

                <p>
                لإكمال إنشاء حسابك، استخدم رمز التأكيد التالي:
                </p>

                <h1>%s</h1>

                <p>هذا الرمز صالح لمدة 10 دقائق.</p>

                <p>
                إذا لم تقم بإنشاء هذا الحساب، يمكنك تجاهل هذه الرسالة.
                </p>

                <p>-- فريق إشارة --</p>
                """.formatted(code);

        sendEmail(
                to,
                "تأكيد بريدك الإلكتروني",
                html
        );
    }

    private void sendEmail(String to, String subject, String html) {

        try {

            restClient.post()
                    .uri("/api/send")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "from", from,
                            "to", to,
                            "subject", subject,
                            "html", html
                    ))
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception e) {

            System.err.println("Failed to send email through Sendlib:");
            e.printStackTrace();

            throw new RuntimeException("فشل إرسال البريد الإلكتروني", e);
        }
    }
}