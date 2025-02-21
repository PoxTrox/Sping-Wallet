package org.example.spingwallet.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNotificationEvent {


    private UUID userId;

    private String email;

    private BigDecimal amount;

    private LocalDateTime paymentDate;



}
