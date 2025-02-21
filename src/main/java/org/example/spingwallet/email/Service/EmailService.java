package org.example.spingwallet.email.Service;

import org.example.spingwallet.web.dto.PaymentNotificationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @EventListener
    @Async
    public void sentEmailWhenChargeIsActive(PaymentNotificationEvent event) {
        System.out.printf("Charge happened for User with id [%s]\n", event.getUserId());
    }
}
