package org.example.spingwallet.subscription.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.spingwallet.exception.DomainException;
import org.example.spingwallet.subscription.model.Subscription;
import org.example.spingwallet.subscription.model.SubscriptionPeriod;
import org.example.spingwallet.subscription.model.SubscriptionStatus;
import org.example.spingwallet.subscription.model.SubscriptionType;
import org.example.spingwallet.subscription.repository.SubscriptionRepository;
import org.example.spingwallet.transaction.model.Transaction;
import org.example.spingwallet.transaction.model.TransactionStatus;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.wallet.service.WalletService;
import org.example.spingwallet.web.dto.UpgradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class SubscriptionService {


    private final SubscriptionRepository subscriptionRepository;
    private final WalletService walletService;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, WalletService walletService) {
        this.subscriptionRepository = subscriptionRepository;
        this.walletService = walletService;
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

    @Transactional
    public Transaction upgrade(User user, SubscriptionType subscriptionType, UpgradeRequest upgradeRequest) {

        Optional<Subscription> byStatusAndOwnerId = subscriptionRepository.findByStatusAndOwnerId(SubscriptionStatus.ACTIVE, user.getId());
        if (byStatusAndOwnerId.isEmpty()) {
            throw new DomainException("No active subscription with id[%s] and type[%s]".formatted(user.getId(), subscriptionType.name()));
        }

        Subscription currentSubscription = byStatusAndOwnerId.get();

        SubscriptionPeriod subscriptionPeriod = upgradeRequest.getSubscriptionPeriod();
        BigDecimal price = getSubscriptionPrice(subscriptionType, subscriptionPeriod);

        String description = "Purchase Subscription for %s.".formatted(price);
        Transaction chargeResult = walletService.charge(user, upgradeRequest.getWalletId(), price, description);

        if (chargeResult.getStatus() == TransactionStatus.FAILED) {
            log.warn("Charge failed with user id [%s] subscription-type [%s]".formatted(user.getId(), subscriptionType.name()));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationDate;
        if (SubscriptionPeriod.MONTHLY.equals(subscriptionPeriod)) {
            expirationDate = now.plusMonths(1);
        } else {
            expirationDate = now.plusYears(1);
        }
        Subscription NewSubscription = Subscription.builder()
                .owner(user)
                .status(SubscriptionStatus.ACTIVE)
                .period(subscriptionPeriod)
                .type(subscriptionType)
                .price(price)
                .renewalAllowed(subscriptionPeriod == SubscriptionPeriod.MONTHLY)
                .creationDate(LocalDateTime.now())
                .expirationDate(expirationDate).
                build();

        currentSubscription.setExpirationDate(now);
        currentSubscription.setStatus(SubscriptionStatus.COMPLETED);

        subscriptionRepository.save(currentSubscription);
        subscriptionRepository.save(NewSubscription);


        return chargeResult;
    }

    private BigDecimal getSubscriptionPrice(SubscriptionType subscriptionType, SubscriptionPeriod subscriptionPeriod) {

        if (SubscriptionType.DEFAULT.equals(subscriptionType)) {
            return BigDecimal.ZERO;
        } else if (SubscriptionType.PREMIUM.equals(subscriptionType) && subscriptionPeriod.equals(SubscriptionPeriod.MONTHLY)) {
            return new BigDecimal("19.99");
        } else if (SubscriptionType.PREMIUM.equals(subscriptionType) && subscriptionPeriod.equals(SubscriptionPeriod.YEARLY)) {
            return new BigDecimal("150.99");
        } else if (SubscriptionType.ULTIMATE.equals(subscriptionType) && subscriptionPeriod.equals(SubscriptionPeriod.MONTHLY)) {
            return new BigDecimal("39.99");
        } else if (SubscriptionType.ULTIMATE.equals(subscriptionType) && subscriptionPeriod.equals(SubscriptionPeriod.YEARLY)) {
            return new BigDecimal("450.99");
        }


        return null;
    }
}
