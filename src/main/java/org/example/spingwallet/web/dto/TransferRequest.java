package org.example.spingwallet.web.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder

public class TransferRequest {

    @NotNull (message = "Field Must be not empty")
    private UUID fromWalletId;

    @NotNull (message = "Field must not be empty")
    private String toUserName;
    @Positive (message = "Amount must be positive number and greater then 0")
    @Min(1)
    private BigDecimal amount;




}
