package org.example.spingwallet.web.dto;

import lombok.Builder;
import lombok.Data;
import org.example.spingwallet.subscription.model.SubscriptionPeriod;

import java.util.UUID;

@Data
@Builder

public class UpgradeRequest  {

    private SubscriptionPeriod subscriptionPeriod;

    private UUID walletId;

}
