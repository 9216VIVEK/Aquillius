package com.aquillius.portal.service;

import com.aquillius.portal.dto.MembershipPurchaseDto;
import com.aquillius.portal.dto.PurchaseDto;
import com.aquillius.portal.entity.Purchase;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.enums.PaymentStatus;
import com.aquillius.portal.model.Amount;
import com.aquillius.portal.model.FinalAmount;

import java.time.LocalDateTime;

public interface PurchaseService {

    void save(Purchase purchase);

    Purchase generateMembershipPurchase(LocalDateTime currentDateTime,
                                        PaymentStatus paymentStatus, FinalAmount amount)throws Exception;
}
