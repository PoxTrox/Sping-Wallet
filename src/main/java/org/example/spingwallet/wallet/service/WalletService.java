package org.example.spingwallet.wallet.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.spingwallet.email.Service.EmailService;
import org.example.spingwallet.exception.DomainException;
import org.example.spingwallet.subscription.model.Subscription;
import org.example.spingwallet.subscription.model.SubscriptionType;
import org.example.spingwallet.tracking.Service.TrackingService;
import org.example.spingwallet.transaction.model.Transaction;
import org.example.spingwallet.transaction.model.TransactionStatus;
import org.example.spingwallet.transaction.model.TransactionType;
import org.example.spingwallet.transaction.service.TransactionService;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.wallet.model.Wallet;
import org.example.spingwallet.wallet.model.WalletStatus;
import org.example.spingwallet.wallet.repository.WalletRepository;
import org.example.spingwallet.web.dto.PaymentNotificationEvent;
import org.example.spingwallet.web.dto.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionService transactionService;
    private final ApplicationEventPublisher eventPublisher;


    private static final String SMART_WALLET_SENDER = "SMART WALLET SENDER";

    @Autowired
    public WalletService(WalletRepository walletRepository, TransactionService transactionService, EmailService emailService, TrackingService trackingService, ApplicationEventPublisher eventPublisher) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
        this.eventPublisher = eventPublisher;

    }


    public void   unlockMoreWallets(User user) {

        List<Wallet> allByOwnerUsername = walletRepository.findAllByOwnerUsername(user.getUsername());
        Subscription subscription = user.getSubscriptions().get(0);

        boolean isDefaultPlanAndMaxWalletUnlocked = subscription.getType() == SubscriptionType.DEFAULT && allByOwnerUsername.size() == 1;
        boolean isPremiumPlanAndMaxWalletUnlocked = subscription.getType() == SubscriptionType.PREMIUM && allByOwnerUsername.size()  == 2;
        boolean isUltimatePlanAndMaxWalletUnlocked = subscription.getType() == SubscriptionType.ULTIMATE && allByOwnerUsername.size()  == 3;

        if(isDefaultPlanAndMaxWalletUnlocked || isPremiumPlanAndMaxWalletUnlocked || isUltimatePlanAndMaxWalletUnlocked) {
            throw new DomainException("Cannot unlock more wallets");
        }

        Wallet wallet =  Wallet.builder()
                .owner(user)
                .status(WalletStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .currency(Currency.getInstance("EUR"))
                .creationDate(LocalDateTime.now())
                .lastUpdateDate(LocalDateTime.now())
                .build();
        walletRepository.save(wallet);

    }

    public Wallet createWallet(User user) {

        List<Wallet> allByOwnerUsername = walletRepository.findAllByOwnerUsername(user.getUsername());
        if (!allByOwnerUsername.isEmpty()) {
            throw new DomainException("This username  {%s} already has wallet ".formatted(user.getUsername()));
        }
        Wallet wallet = initializeWallet(user);

        walletRepository.save(wallet);
        log.info("Wallet created new wallet with id [%s] and balance [%.2f]".formatted(wallet.getId(), wallet.getBalance()));

        return wallet;
    }

    @Transactional
    public Transaction topUp(UUID uuid, BigDecimal amount) {

        Wallet wallet = getWallerID(uuid);
        String transactionDescription = "Top up %.2f".formatted(amount.doubleValue());

        if (wallet.getStatus() == WalletStatus.INACTIVE) {
            return transactionService.createNewTransaction(wallet.getOwner(),
                    SMART_WALLET_SENDER,
                    String.valueOf(uuid),
                    amount,
                    wallet.getBalance(),
                    wallet.getCurrency(),
                    TransactionType.DEPOSIT,
                    TransactionStatus.FAILED,
                    transactionDescription,
                    "Inactive Wallet"
            );
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setLastUpdateDate(LocalDateTime.now());
        walletRepository.save(wallet);

        return transactionService.createNewTransaction(wallet.getOwner(),
                SMART_WALLET_SENDER,
                String.valueOf(uuid),
                amount,
                wallet.getBalance(),
                wallet.getCurrency(),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCEEDED,
                transactionDescription,
                null
        );
    }

    private Wallet getWallerID(UUID uuid) {
        return walletRepository.findById(uuid).orElseThrow(() -> new DomainException("Wallet with id[%s] does not exist".formatted(uuid)));
    }


    private Wallet initializeWallet(User user) {

        return Wallet.builder()
                .owner(user)
                .status(WalletStatus.ACTIVE)
                .balance(new BigDecimal("69.00"))
                .currency(Currency.getInstance("EUR"))
                .creationDate(LocalDateTime.now())
                .lastUpdateDate(LocalDateTime.now())
                .build();
    }

    @Transactional
    public Transaction charge(User user, UUID id, BigDecimal amount, String description) {

        Wallet waller = getWallerID(id);

        boolean isFailedTransaction = false;
        String failureReason = null;
        if (waller.getStatus() == WalletStatus.INACTIVE) {
            isFailedTransaction = true;
            failureReason = "Inactive Wallet";

        }

        if (waller.getBalance().compareTo(amount) < 0) {
            failureReason = "Insufficient balance";
            isFailedTransaction = true;
        }

        if (isFailedTransaction) {
            return transactionService.createNewTransaction(
                    user,
                    waller.getId().toString(),
                    SMART_WALLET_SENDER,
                    amount,
                    waller.getBalance(),
                    waller.getCurrency(),
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.FAILED,
                    description,
                    failureReason
            );
        }

        waller.setBalance(waller.getBalance().subtract(amount));
        waller.setLastUpdateDate(LocalDateTime.now());
        walletRepository.save(waller);

        PaymentNotificationEvent paymentNotificationEvent = PaymentNotificationEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .amount(amount)
                .paymentDate(LocalDateTime.now())
                .build();

        eventPublisher.publishEvent(paymentNotificationEvent);


        return transactionService.createNewTransaction(user,
                waller.getId().toString(),
                SMART_WALLET_SENDER,
                amount,
                waller.getBalance(),
                waller.getCurrency(),
                TransactionType.WITHDRAWAL,
                TransactionStatus.SUCCEEDED,
                description,
                null);
    }

    public Transaction transfersFunds() {
        return null;
    }


    public Transaction transfersFunds(User sender, TransferRequest transferRequest) {

        Wallet senderWallet = getWallerID(transferRequest.getFromWalletId());
        Optional<Wallet> receiverWallet = walletRepository.findAllByOwnerUsername(transferRequest.getToUserName()).stream().filter(e -> e.getStatus() == WalletStatus.ACTIVE).findFirst();
        String transferDescription = "Transfer from %s to %s for %s".formatted(sender.getUsername(), transferRequest.getToUserName(), transferRequest.getAmount());
        if (receiverWallet.isEmpty()) {


            return transactionService.createNewTransaction(sender,
                    sender.getId().toString(),
                    transferRequest.getToUserName(),
                    transferRequest.getAmount(),
                    senderWallet.getBalance(),
                    senderWallet.getCurrency(),
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.FAILED,
                    transferDescription,
                    "Invalid criteria for transfer");


        }

        Transaction senderWithdraw = charge(sender, senderWallet.getId(), transferRequest.getAmount(), transferDescription);

        if (senderWithdraw.getStatus() == TransactionStatus.FAILED) {
            return senderWithdraw;
        }

        Wallet recieverWallet = receiverWallet.get();
        recieverWallet.setBalance(recieverWallet.getBalance().add(transferRequest.getAmount()));
        recieverWallet.setLastUpdateDate(LocalDateTime.now());
        walletRepository.save(recieverWallet);

        transactionService.createNewTransaction(recieverWallet.getOwner(),
                senderWallet.getId().toString(),
                recieverWallet.getId().toString(),
                transferRequest.getAmount(),
                recieverWallet.getBalance(),
                recieverWallet.getCurrency(),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCEEDED,
                transferDescription,
                null
        );

        return senderWithdraw;
    }

    public Map<UUID, List<Transaction>> getLastFourTransaction(List<Wallet> wallets) {

        Map<UUID, List<Transaction>> lastFourTransactions = new LinkedHashMap<>();

        for (Wallet wallet : wallets) {

           List<Transaction> lastTransaction = transactionService.getLastFourTransactionById(wallet);
            lastFourTransactions.put(wallet.getId(), lastTransaction);
        }


        return lastFourTransactions;
    }

    public void switchStatus(UUID id, UUID detailsId) {

        Optional<Wallet> optionalWallet = walletRepository.findByIdAndOwnerId(id, detailsId);

        if(optionalWallet.isEmpty()) {
            throw new DomainException("Wallet with id {%s} does not belong to user id {%s}".formatted(id.toString(), detailsId.toString()));
        }
        Wallet wallet = optionalWallet.get();
        if(wallet.getStatus() == WalletStatus.ACTIVE) {
            wallet.setStatus(WalletStatus.INACTIVE);
        }else {
            wallet.setStatus(WalletStatus.ACTIVE);
        }
        walletRepository.save(wallet);

    }
}
