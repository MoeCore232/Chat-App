package com.chat_app.chat_app.Shared.Notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationsService {

    @Autowired
    private RestTemplate restTemplate;

    public void sendNotification(
            String expoPushToken,
            String title,
            String body
    ) {

        String url = "https://exp.host/--/api/v2/push/send";

        Map<String, Object> notification = new HashMap<>();

        notification.put("to", expoPushToken);
        notification.put("title", title);
        notification.put("body", body);
        notification.put("sound", "default");

        restTemplate.postForObject(
                url,
                notification,
                String.class
        );
    }

    public String testNotification(String expoPushToken) {

        String url = "https://exp.host/--/api/v2/push/send";

        Map<String, Object> notification = new HashMap<>();

        notification.put("to", expoPushToken);
        notification.put("title", "اختبار");
        notification.put("body", "هذه رسالة اختبار من Spring Boot");
        notification.put("sound", "default");

        return restTemplate.postForObject(
                url,
                notification,
                String.class
        );
    }
}
