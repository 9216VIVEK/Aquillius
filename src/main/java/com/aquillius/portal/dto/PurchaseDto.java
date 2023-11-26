package com.aquillius.portal.dto;

import com.aquillius.portal.enums.PurchasableItem;
import com.aquillius.portal.model.Amount;
import lombok.Data;

import java.time.LocalDate;


public record PurchaseDto (

    PurchasableItem purchasableItem,

    MembershipPurchaseDto membershipPurchaseDto,

    Amount amount)

{}
