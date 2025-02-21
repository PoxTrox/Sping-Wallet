package org.example.spingwallet.user.service;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.spingwallet.exception.DomainException;
import org.example.spingwallet.security.AuthenticationDetails;
import org.example.spingwallet.subscription.model.Subscription;
import org.example.spingwallet.subscription.service.SubscriptionService;
import org.example.spingwallet.user.model.Country;
import org.example.spingwallet.user.model.User;
import org.example.spingwallet.user.model.UserRole;
import org.example.spingwallet.user.repository.UserRepository;
import org.example.spingwallet.wallet.model.Wallet;
import org.example.spingwallet.wallet.service.WalletService;
import org.example.spingwallet.web.dto.LoginRequest;
import org.example.spingwallet.web.dto.RegisterRequest;
import org.example.spingwallet.web.dto.UserEditRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionService subscriptionService;
    private final WalletService walletService;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       SubscriptionService subscriptionService,
                       WalletService walletService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionService = subscriptionService;
        this.walletService = walletService;
    }


    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public User register(RegisterRequest registerRequest) {

        Optional<User> optionUser = userRepository.findByUsername(registerRequest.getUsername());
        if (optionUser.isPresent()) {
            throw new DomainException("Username [%s] already exist.".formatted(registerRequest.getUsername()));
        }

        User user = userRepository.save(initializeUser(registerRequest));

        Subscription defaultSubscription = subscriptionService.createDefaultSubscription(user);
        user.setSubscriptions(List.of(defaultSubscription));

        Wallet standardWallet = walletService.createWallet(user);
        user.setWallets(List.of(standardWallet));


        log.info("Successfully create new user account for username [%s] and id [%s]".formatted(user.getUsername(), user.getId()));

        return user;
    }

    private User initializeUser(RegisterRequest registerRequest) {
        LocalDateTime now = LocalDateTime.now();

        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .isActive(true)
                .country(Country.valueOf(registerRequest.getCountry()))
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Cacheable("users")
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public User getById(UUID id) {

        return userRepository.findById(id).orElseThrow(() -> new DomainException("User with id [%s] does not exist.".formatted(id)));
    }

    @CacheEvict(value = "users",allEntries = true)
    public void editUser(UUID id, UserEditRequest userEditRequest) {

        User userById = getById(id);

        userById.setFirstName(userEditRequest.getFirstName());
        userById.setLastName(userEditRequest.getLastName());
        userById.setEmail(userEditRequest.getEmail());
        userById.setProfilePicture(userEditRequest.getProfilePicture());
        userRepository.save(userById);


    }


    public void changeUserRole(UUID id) {

        User byId = getById(id);

        if(byId.getRole() == UserRole.USER) {
            byId.setRole(UserRole.ADMIN);
        }else {
            byId.setRole(UserRole.USER);
        }
        userRepository.save(byId);
    }
    @CacheEvict(value = "users",allEntries = true)
    public void changeStatus(UUID id) {

        User byId = getById(id);
        byId.setActive(!byId.isActive());

        userRepository.save(byId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new DomainException("Username [%s] does not exist.".formatted(username)));

        return new AuthenticationDetails(user.getId(),user.getUsername(), user.getPassword(),user.getRole(),user.isActive());
    }
}
