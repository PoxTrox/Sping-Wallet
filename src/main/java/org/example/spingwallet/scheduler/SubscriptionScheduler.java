package org.example.spingwallet.scheduler;


import lombok.extern.slf4j.Slf4j;
import org.example.spingwallet.subscription.model.Subscription;
import org.example.spingwallet.subscription.model.SubscriptionPeriod;
import org.example.spingwallet.subscription.model.SubscriptionType;
import org.example.spingwallet.subscription.service.SubscriptionService;
import org.example.spingwallet.transaction.model.Transaction;
import org.example.spingwallet.transaction.model.TransactionStatus;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.web.dto.UpgradeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class SubscriptionScheduler {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionScheduler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Scheduled(fixedRate = 30000)
    public void renewSubscription() {

        List<Subscription> allSubscriptionsForRenewal = subscriptionService.getAllSubscriptionsForRenewal();

        if(allSubscriptionsForRenewal.isEmpty()) {
            log.info("No Subscriptions found for renewal");
        }


        for(Subscription subscription : allSubscriptionsForRenewal) {

            if(subscription.isRenewalAllowed()){
                User owner = subscription.getOwner();
                SubscriptionType type = subscription.getType();
                SubscriptionPeriod period = subscription.getPeriod();
                UUID walledID = subscription.getOwner().getWallets().get(0).getId();
                UpgradeRequest upgradeRequest = UpgradeRequest.builder()
                        .subscriptionPeriod(period)
                        .walletId(walledID)
                        .build();
                Transaction transaction = subscriptionService.upgrade(owner, type, upgradeRequest);

                if(transaction.getStatus()== TransactionStatus.FAILED) {
                    subscriptionService.markSubAsTerminated(subscription);
                    subscriptionService.createDefaultSubscription(subscription.getOwner());
                }else {
                    subscriptionService.markSubscriptionAsCompleted(subscription);
                    subscriptionService.createDefaultSubscription(subscription.getOwner());
                }
            }


        }


    }

}
