package org.example.spingwallet.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class RegisterRequest {

    @Size(min = 5, max = 50, message = "User name must be with at least 5 symbols")
    private String username;
    @Size(min = 5, message = "Password must be with at least 5 symbols")
    private String password;
    @NotNull
    private String country;
}
