package org.example.spingwallet.web.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @Size(min = 5, max = 50, message = "User name must be with at least 5 symbols")
    private String username;

    @Size(min = 5, message = "Password must be with at least 5 symbols")
    private String password;
}
