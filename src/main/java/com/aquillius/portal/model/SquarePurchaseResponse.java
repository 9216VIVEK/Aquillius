package com.aquillius.portal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SquarePurchaseResponse {

    PaymentResult paymentResult;

    String invoicePath;
}
