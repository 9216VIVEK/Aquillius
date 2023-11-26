package com.aquillius.portal.controller;

import java.security.Principal;

import com.aquillius.portal.dto.AddOnDto;
import com.aquillius.portal.model.FinalAmount;
import com.aquillius.portal.model.MembershipAmountResponse;
import com.aquillius.portal.repository.FinalAmountRepository;
import com.aquillius.portal.repository.MembershipTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aquillius.portal.dto.MembershipPurchaseDto;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.model.Amount;
import com.aquillius.portal.model.Message;
import com.aquillius.portal.service.MembershipService;
import com.aquillius.portal.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/portal/purchase")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class PurchaseController {

    private final MembershipService membershipService;

    private final MembershipTypeRepository membershipTypeRepository;

    private final UserService userService;

    private final FinalAmountRepository finalAmountRepository;

    @PutMapping("/membership")
    public ResponseEntity<?> getMembershipPurchaseAmount(@RequestBody MembershipPurchaseDto dto, Principal principal) {
        log.info("=================inside get membership purchase amount api in Purchase Controller====================");
        boolean isValid = checkValid(dto);
        if(!isValid) return ResponseEntity.ok(new Message(0, "Number of memberships should be more than or equal to total number of add ons"));
        try {
            log.debug("inside try block in getMembershipPurchaseAmount in Purchase Controller");
            User user = userService.findByEmail(principal.getName());
            if (dto.payNow() == 1) return payNow(user);
            FinalAmount amount = membershipService.getMembershipPurchaseAmount(dto, user);
            amount.setInitialPurchase(1);
            finalAmountRepository.save(amount);
            return ResponseEntity.ok(amount);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new Message(0, "Something went Wrong"));
        }
    }

    private boolean checkValid(MembershipPurchaseDto dto) {
        int totalNoOfAddOns = dto.addOnList().stream()
                .map(AddOnDto::getQuantity)
                .reduce(0, Integer::sum);
        return dto.noOfMemberships() >= totalNoOfAddOns;
    }

    public ResponseEntity<?> payNow(User user) {
        log.info("=================inside payNow api in Purchase Controller====================");
        FinalAmount amount = userService.payNow(user);
        amount.setInitialPurchase(0);
        finalAmountRepository.save(amount);
        return ResponseEntity.ok(amount);
    }

}
