package org.example.spingwallet.wallet.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.spingwallet.exception.DomainException;
import org.example.spingwallet.transaction.model.Transaction;
import org.example.spingwallet.transaction.model.TransactionStatus;
import org.example.spingwallet.transaction.model.TransactionType;
import org.example.spingwallet.transaction.service.TransactionService;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.wallet.model.Wallet;
import org.example.spingwallet.wallet.model.WalletStatus;
import org.example.spingwallet.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Slf4j
@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionService transactionService;

    private static final String SMART_WALLET_SENDER = "SMART WALLET SENDER";

    @Autowired
    public WalletService(WalletRepository walletRepository, TransactionService transactionService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    public Wallet createWallet(User user) {

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
}
