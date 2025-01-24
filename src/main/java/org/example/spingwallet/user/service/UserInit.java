package org.example.spingwallet.user.service;

import org.example.spingwallet.user.model.Country;
import org.example.spingwallet.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserInit  implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public UserInit(UserService userService) {
        this.userService = userService;
    }


    @Override
    public void run(String... args) throws Exception {

        if(!userService.getAllUsers().isEmpty()) {
            return;
        }

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("admin123")
                .password("admin123")
                .country(Country.BULGARIA.toString())
                .build();

        userService.register(registerRequest);

    }
}
