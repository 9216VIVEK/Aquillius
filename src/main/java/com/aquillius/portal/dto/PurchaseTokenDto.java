package com.aquillius.portal.dto;

import com.aquillius.portal.model.Amount;
import com.aquillius.portal.model.FinalAmount;
import com.aquillius.portal.model.TokenWrapper;
import lombok.Data;

public record PurchaseTokenDto (

    TokenWrapper tokenWrapper,

    MembershipPurchaseDto purchaseDto,

    Long amountId)

{}
