package com.aquillius.portal.service;

import com.aquillius.portal.dto.PurchaseTokenDto;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.model.FinalAmount;

public interface SquarePaymentService {


    String makeMembershipPayment(PurchaseTokenDto purchaseTokenDto, User user, FinalAmount amount) throws Exception;
}
