package com.example.application.services;

import com.vaadin.flow.server.webpush.WebPush;
import com.vaadin.flow.server.webpush.WebPushException;
import com.vaadin.flow.server.webpush.WebPushMessage;
import com.vaadin.flow.server.webpush.WebPushSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebPushService {

    @Value("${public.key}")
    private String publicKey;
    @Value("${private.key}")
    private String privateKey;
    @Value("${subject}")
    private String subject;

    private final Map<String, WebPushSubscription> endpointToSubscription = new HashMap<>();

    WebPush webPush;

    /**
     * Initialize security and push service for initial get request.
     *
     * @throws GeneralSecurityException security exception for security complications
     */
    public WebPush getWebPush() {
        if(webPush == null) {
            webPush = new WebPush(publicKey, privateKey, subject);
        }
        return webPush;
    }

    /**
     * WebPushOptions for additional information for the web push notification
     * @param body - body string for the notification
     * @param data - notification data
     * @param icon - icon for the notification
     */
    public record WebPushOptions(String body,
                                 Serializable data,
                                 String icon) implements Serializable {
    }

    /**
     *
     * @param url - url for target view
     */
    public record WebPushData(String url) implements Serializable {}

    public void notifyAll(String title, String body, String url) {
        endpointToSubscription.values().forEach(subscription -> {

            WebPushOptions webPushOptions = new WebPushOptions(
                    body,
                    new WebPushData(url),
                    LineAwesomeIconUrl.BELL.toString()
            );
            try {
                webPush.sendNotification(subscription, new WebPushMessage(title, webPushOptions));
            } catch (WebPushException e) {
                getLogger().error(e.getMessage());
            }
        });
    }

    /**
     * Send a notification to all subscriptions.
     *
     * @param title message title
     * @param body message body
     */
    public void notifyAll(String title, String body) {
        endpointToSubscription.values().forEach(subscription ->
                webPush.sendNotification(subscription, new WebPushMessage(title, body)));
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(WebPushService.class);
    }

    public void store(WebPushSubscription subscription) {
        getLogger().info("Subscribed to ", subscription.endpoint());
        /*
         * Note, in a real world app you'll want to persist these
         * in the backend. Also, you probably want to know which
         * subscription belongs to which user to send custom messages
         * for different users. In this demo, we'll just use
         * endpoint URL as key to store subscriptions in memory.
         */
        endpointToSubscription.put(subscription.endpoint(), subscription);
    }


    public void remove(WebPushSubscription subscription) {
        getLogger().info("Unsubscribed ", subscription.endpoint());
        endpointToSubscription.remove(subscription.endpoint());
    }

    public boolean isEmpty() {
        return endpointToSubscription.isEmpty();
    }

}
