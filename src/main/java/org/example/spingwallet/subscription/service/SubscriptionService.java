package org.example.spingwallet.subscription.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spingwallet.subscription.model.Subscription;
import org.example.spingwallet.subscription.model.SubscriptionPeriod;
import org.example.spingwallet.subscription.model.SubscriptionStatus;
import org.example.spingwallet.subscription.model.SubscriptionType;
import org.example.spingwallet.subscription.repository.SubscriptionRepository;
import org.example.spingwallet.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class SubscriptionService {


    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription createDefaultSubscription(User user) {

        Subscription subscription = initializeSubscription(user);
        subscriptionRepository.save(subscription);
        log.info("subscription created: with id[id] and type[%s]".formatted(subscription.getId()), subscription.getType());
        return subscription;
    }

    private Subscription initializeSubscription(User user) {

    LocalDateTime now = LocalDateTime.now();

        return Subscription.builder()
                .owner(user)
                .status(SubscriptionStatus.ACTIVE)
                .type(SubscriptionType.DEFAULT)
                .period(SubscriptionPeriod.MONTHLY)
                .price(new BigDecimal("0.00"))
                .renewalAllowed(true)
                .creationDate(now)
                .expirationDate(now.plusMonths(1))
                .build();
    }

}
