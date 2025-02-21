package org.example.spingwallet.tracking.Service;

import org.example.spingwallet.web.dto.PaymentNotificationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {

    @EventListener
    @Async
    public void trackNewPayment (PaymentNotificationEvent event) {

        System.out.printf("New payment for user [%s] happened",event.getUserId());
    }
}
