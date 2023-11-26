package com.aquillius.portal.util;

import com.aquillius.portal.dto.AddOnDto;
import com.aquillius.portal.dto.PurchaseDto;
import com.aquillius.portal.entity.*;
import com.aquillius.portal.enums.AnnualPaymentType;
import com.aquillius.portal.enums.PurchasableItem;
import com.aquillius.portal.model.Amount;
import com.aquillius.portal.repository.AddOnTypeRepository;
import com.aquillius.portal.repository.AmountRepository;
import com.aquillius.portal.repository.MembershipTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CalculateAmount {

    @Autowired
    MembershipTypeRepository membershipTypeRepository;

    @Autowired
    AddOnTypeRepository addOnTypeRepository;


//    public Amount calculateMembershipAmount(MembershipType purchaseMembershipType, Membership currentMembership) {
//        log.info("===============inside calculateMembershipAmount in CalculateAmount class=============");
//        Amount amount = new Amount();
//        LocalDate currentDate = LocalDate.now();
//        float currentMembershipPrice = currentMembership.getType().getMonthlyPrice();
//        LocalDate dueDate = currentMembership.getDueDate();
//        float finePercentage = currentDate.isAfter(dueDate) ? 0.10f : 0;
//        float lateFee = currentMembershipPrice * finePercentage;
//        amount.setItem(currentMembership.getType().getName());
//        amount.setAmount(getFormattedFloat(purchaseMembershipType.getMonthlyPrice()));
//        amount.setLateFee(getFormattedFloat(lateFee));
//        amount.setTotalAmount(getFormattedFloat(purchaseMembershipType.getMonthlyPrice() + lateFee));
//        log.info("inside calculateMembershipAmount in CalculateAmount class = " + amount.getTotalAmount());
//        return amount;
//    }

    public Amount calculateCurrentMembershipAmount(Membership currentMembership) {
        log.info("===============inside calculateMembershipAmount in CalculateAmount class=============");
        Amount amount = new Amount();
        LocalDate currentDate = LocalDate.now();
        int quantity = currentMembership.getQuantity();
        log.debug("Quantity = " + quantity);
        float currentMembershipPrice = currentMembership.getType().getMonthlyPrice();
        log.debug("Current Membership Price = " + currentMembershipPrice);
        LocalDate dueDate = currentMembership.getDueDate();
        log.debug("Due Date = " + dueDate);
        float finePercentage = currentDate.isAfter(dueDate) ? 0.10f : 0;
        float lateFee = currentMembershipPrice * finePercentage * quantity;
        log.debug("Late fee = " + lateFee);
        amount.setItem(currentMembership.getType().getName());
        amount.setQuantity(currentMembership.getQuantity());
        amount.setAmount(getFormattedFloat(currentMembership.getType().getMonthlyPrice()) * quantity);
        amount.setPrice(currentMembership.getType().getMonthlyPrice());
        amount.setTotalPrice(getFormattedFloat(currentMembership.getType().getMonthlyPrice() * quantity));
        amount.setLateFee(getFormattedFloat(lateFee));
        amount.setTotalAmount(getFormattedFloat(currentMembership.getType().getMonthlyPrice() * quantity + lateFee));
        log.info("inside calculateMembershipAmount in CalculateAmount class = " + amount.getTotalAmount());
        return amount;
    }


    public Amount calculateNewMembershipAmount(MembershipType purchaseMembershipType, LocalDate startDate) {
        log.info("===============inside calculateNewMembershipAmount in CalculateAmount class=============");
        Amount amount = new Amount();
        YearMonth currentYearMonth = YearMonth.from(startDate);
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int remainingDays = daysInMonth - startDate.getDayOfMonth();
        float cost = ((float) purchaseMembershipType.getMonthlyPrice() / daysInMonth) * remainingDays;
        float formattedCost = getFormattedFloat(cost);
        amount.setLateFee(0);
        amount.setAmount(formattedCost);
        amount.setTotalAmount(formattedCost);
        return amount;
    }


    public float getFormattedFloat(float myFloat) {
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedFloat = df.format(myFloat);
        return Float.parseFloat(formattedFloat);
    }

    @Autowired
    AmountRepository amountRepository;

    public List<Amount> calculateAddOnsPrice(List<AddOnDto> addOnList, AnnualPaymentType paymentType,
                                             LocalDate startDate, List<Amount> amountList) {
        if (addOnList == null) return amountList;
        addOnList.stream()
                .filter(addOnDto -> addOnDto.getQuantity() > 0)
                .forEach(addOnDto -> {
                    Optional<AddOnType> addOnTypeOptional = addOnTypeRepository.findById(addOnDto.getId());
                    AddOnType addOnType = addOnTypeOptional.get();
                    Amount amount;
//                    String item, float price, float totalPrice, int quantity,
//                  float totalAmount, float amount, float lateFee
                    if (paymentType == AnnualPaymentType.ANNUAL) {
                        amount = new Amount(addOnType.getName(), addOnType.getAnnualPrice(),
                                addOnType.getAnnualPrice() * addOnDto.getQuantity(), addOnDto.getQuantity(),
                                addOnType.getAnnualPrice() * addOnDto.getQuantity(),
                                addOnType.getAnnualPrice() * addOnDto.getQuantity(), 0);
                    } else {
                        YearMonth currentYearMonth = YearMonth.from(startDate);
                        int daysInMonth = currentYearMonth.lengthOfMonth();
                        int remainingDays = daysInMonth - startDate.getDayOfMonth();
                        float cost = ((addOnType.getMonthlyPrice() / daysInMonth) * remainingDays) * addOnDto.getQuantity();
                        float formattedCost = getFormattedFloat(cost);
                        amount = new Amount(addOnType.getName(), addOnType.getMonthlyPrice(),
                                addOnType.getMonthlyPrice() * addOnDto.getQuantity(),
                                addOnDto.getQuantity(), formattedCost, formattedCost, 0);
                    }
                    amountRepository.save(amount);
                    amountList.add(amount);
                });
        return amountList;
    }

    public float calculateTotalAmountListPrice(List<Amount> amountList) {
        float total = 0;
        for (Amount a : amountList) {
            total += a.getTotalAmount();
        }
        return getFormattedFloat(total);
    }

    public List<Amount> calculateAddOnsPrice(List<AddOn> addOnList, boolean isLateFee, List<Amount> amountList) {
        addOnList.stream()
                .filter(addOn -> addOn.getQuantity() > 0)
                .forEach(addOn -> {
                    AddOnType addOnType = addOn.getAddOnType();
                    Amount amount;
                    float lateFee = 0;
                    if (isLateFee) {
                        lateFee = addOnType.getMonthlyPrice() * addOn.getQuantity() * 0.10f;
                    }
                    //                    String item, float price, float totalPrice, int quantity,
//                  float totalAmount, float amount, float lateFee
                    amount = new Amount(addOnType.getName(), addOnType.getMonthlyPrice(),
                            addOnType.getMonthlyPrice() * addOn.getQuantity(), addOn.getQuantity(),
                            addOnType.getMonthlyPrice() * addOn.getQuantity() + lateFee,
                            addOnType.getMonthlyPrice() * addOn.getQuantity(), lateFee);
                    amountRepository.save(amount);
                    amountList.add(amount);
                });
        return amountList;
    }

    public Amount calculateCurrentAddOnAmount(AddOn a) {
        log.info("===============inside calculateCurrentAddOnAmount in CalculateAmount class=============");
        Amount amount = new Amount();
        LocalDate currentDate = LocalDate.now();
        int quantity = a.getQuantity();
        float currentAddOnPrice = a.getAddOnType().getMonthlyPrice();
        log.debug("currentAddOnPrice = " + currentAddOnPrice);
        LocalDate dueDate = a.getDueDate();
        float finePercentage = currentDate.isAfter(dueDate) ? 0.10f : 0;
        float lateFee = currentAddOnPrice * finePercentage * quantity;
        amount.setItem(a.getAddOnType().getName());
        amount.setQuantity(a.getQuantity());
        amount.setAmount(getFormattedFloat(a.getAddOnType().getMonthlyPrice()) * quantity);
        amount.setLateFee(getFormattedFloat(lateFee));
        amount.setTotalAmount(getFormattedFloat(a.getAddOnType().getMonthlyPrice() * quantity + lateFee));
        amount.setPrice(currentAddOnPrice);
        amount.setTotalPrice(getFormattedFloat(currentAddOnPrice * quantity));
        amount.setLateFee(getFormattedFloat(lateFee));
        amount.setTotalAmount(getFormattedFloat(currentAddOnPrice * quantity + lateFee));
        return amount;
    }
}
