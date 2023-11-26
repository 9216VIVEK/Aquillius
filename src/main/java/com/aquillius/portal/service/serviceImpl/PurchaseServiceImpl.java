package com.aquillius.portal.service.serviceImpl;

import com.aquillius.portal.dto.MembershipPurchaseDto;
import com.aquillius.portal.dto.PurchaseDto;
import com.aquillius.portal.entity.Purchase;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.enums.PaymentStatus;
import com.aquillius.portal.enums.PurchasableItem;
import com.aquillius.portal.model.Amount;
import com.aquillius.portal.model.FinalAmount;
import com.aquillius.portal.repository.PurchaseRepository;
import com.aquillius.portal.service.MembershipService;
import com.aquillius.portal.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {


    private final PurchaseRepository purchaseRepository;

    public void save(Purchase purchase) {
        purchaseRepository.save(purchase);
    }

    public Purchase generateMembershipPurchase(LocalDateTime currentDateTime,
                                               PaymentStatus paymentStatus, FinalAmount amount) throws Exception {
    	
		log.info("===================inside generateMembershipPurchase Service API ===================");

        Purchase purchase=new Purchase();
        purchase.setPurchasableItem(PurchasableItem.MEMBERSHIP);
        purchase.setPurchaseDateTime(currentDateTime);
        purchase.setPaymentStatus(paymentStatus);
        purchase.setAmount(amount);
        return purchase;
    }
}
