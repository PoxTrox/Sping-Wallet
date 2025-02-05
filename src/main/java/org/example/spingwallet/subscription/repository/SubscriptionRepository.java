package org.example.spingwallet.subscription.repository;

import org.example.spingwallet.subscription.model.Subscription;
import org.example.spingwallet.subscription.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.rmi.server.UID;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findByStatusAndOwnerId (SubscriptionStatus status, UUID ownerId);
}
