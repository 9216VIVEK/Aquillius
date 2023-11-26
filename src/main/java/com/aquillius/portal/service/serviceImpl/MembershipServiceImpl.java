package com.aquillius.portal.service.serviceImpl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import com.aquillius.portal.dto.AddOnDto;
import com.aquillius.portal.entity.*;
import com.aquillius.portal.enums.AddOnPlan;
import com.aquillius.portal.model.FinalAmount;
import com.aquillius.portal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aquillius.portal.dto.MembershipPurchaseDto;
import com.aquillius.portal.enums.AnnualPaymentType;
import com.aquillius.portal.enums.MembershipPlan;
import com.aquillius.portal.model.Amount;
import com.aquillius.portal.service.MembershipService;
import com.aquillius.portal.util.CalculateAmount;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;

    private final CalculateAmount calculateAmount;

    private final MembershipTypeRepository membershipTypeRepository;

    private final AddOnTypeRepository addOnTypeRepository;

    private final AddOnRepository addOnRepository;

    private final AmountRepository amountRepository;

    private final FinalAmountRepository finalAmountRepository;
    @Autowired
    UserRepository userRepository;

    //being called only when user is buying membership for the first time or upgrading to a new one
    @Override
    public FinalAmount getMembershipPurchaseAmount(MembershipPurchaseDto dto, User user) {

        log.info("===================inside getMembershipPurchaseAmount dto and user Service API ===================");

        MembershipType membershipType = membershipTypeRepository.findById(dto.membershipTypeId());
        if (membershipType == null) return null;
        if (dto.membershipPlan() == MembershipPlan.ANNUAL.getId()
                && dto.membershipAnnualPaymentType() == MembershipPlan.ANNUAL.getId()) {
            log.info("inside if");
            float price = membershipType.getAnnualPrice();
            List<Amount> amountList = new ArrayList<>();
            Amount amount = new Amount(membershipType.getName(), membershipType.getAnnualPrice(),
                    membershipType.getAnnualPrice() * dto.noOfMemberships(), dto.noOfMemberships(),
                    price * dto.noOfMemberships(), price * dto.noOfMemberships(), 0);
            amountRepository.save(amount);
            amountList.add(amount);
            amountList = calculateAmount.calculateAddOnsPrice(dto.addOnList(), AnnualPaymentType.ANNUAL, dto.startDate(), amountList);
            log.info("price = " + price);
            float total = calculateAmount.calculateTotalAmountListPrice(amountList);
            FinalAmount finalAmount = new FinalAmount(amountList, total);
            finalAmountRepository.save(finalAmount);
            return finalAmount;
        }
        Amount newMembershipAmount =
                calculateAmount.calculateNewMembershipAmount(membershipType, dto.startDate());
        List<Amount> amountList = new ArrayList<>();
        Amount amount = new Amount(membershipType.getName(), membershipType.getMonthlyPrice(),
                membershipType.getMonthlyPrice() * dto.noOfMemberships(), dto.noOfMemberships(),
                newMembershipAmount.getTotalAmount() * dto.noOfMemberships(),
                newMembershipAmount.getAmount() * dto.noOfMemberships(),
                newMembershipAmount.getLateFee());
        amountRepository.save(amount);
        amountList.add(amount);
        amountList = calculateAmount.calculateAddOnsPrice(dto.addOnList(), AnnualPaymentType.MONTHLY, dto.startDate(), amountList);
        float total = calculateAmount.calculateTotalAmountListPrice(amountList);
        FinalAmount finalAmount = new FinalAmount(amountList, total);
        finalAmountRepository.save(finalAmount);
        return finalAmount;
    }


    //called via payNow
//    @Override
//    public Amount getCurrentMembershipPurchaseAmount(User user) {
//        log.info("=============inside getCurrentMembershipPurchaseAmount in ServiceAPI=============");
//        Membership membership = user.getMembership();
//        MembershipType membershipType = membership.getType();
//        return calculateAmount.calculateMembershipAmount(membershipType, user);
//    }

    @Override
    public Membership createMembership(MembershipType membershipType, MembershipPlan membershipPlan,
                                       AnnualPaymentType annualPaymentType, User user, LocalDate startDate,
                                       List<AddOnDto> addOnList, int noOfMemberships) {

        log.info("===================inside createMembership Service API ===================");

        if (membershipPlan == MembershipPlan.ANNUAL) {

            return createAnnualMembership(membershipType, annualPaymentType, startDate, addOnList, noOfMemberships);
        }
        return createMonthlyMembership(membershipType, startDate, addOnList, noOfMemberships);
    }

    private Membership createAnnualMembership(MembershipType membershipType, AnnualPaymentType annualPaymentType,
                                              LocalDate startDate, List<AddOnDto> addOnList, int noOfMemberships) {

        log.info("===================inside createAnnualMembership Service API ===================");

        Membership membership = new Membership();
        membership.setType(membershipType);
        membership.setMonth(YearMonth.of(startDate.getYear(), startDate.getMonth()));
        membership.setMembershipStartDate(startDate);
        membership.setMembershipEndDate(startDate.plusDays(365));
        if (annualPaymentType == AnnualPaymentType.ANNUAL) membership.setDueDate(startDate.plusDays(365));
        else membership.setDueDate(startDate.plusMonths(1).withDayOfMonth(5));
        membership.setMembershipPlan(MembershipPlan.ANNUAL);
        membership.setQuantity(noOfMemberships);

//        if (addOnList != null) {
//            List<AddOn> list = new ArrayList<>();
//            for (AddOnDto dto : addOnList) {
//                if (dto.getQuantity() < 1) continue;
//                Optional<AddOnType> addOnTypeOptional = addOnTypeRepository.findById(dto.getId());
//                AddOnType addOnType = addOnTypeOptional.get();
//                AddOn addOn = new AddOn();
//                addOn.setAddOnType(addOnType);
//                addOn.setQuantity(dto.getQuantity());
//                addOnRepository.save(addOn);
//                list.add(addOn);
//            }
//            if (list.isEmpty()) membership.setAddOns(null);
//            else membership.setAddOns(list);
//        }
        return membership;
    }

    private Membership createMonthlyMembership(MembershipType membershipType, LocalDate startDate,
                                               List<AddOnDto> addOnList, int noOfMemberships) {

        log.info("===================inside createMonthlyMembership Service API ===================");

        Membership membership = new Membership();
        membership.setType(membershipType);
        int year = startDate.getYear();
        int month = startDate.getMonthValue();
        YearMonth yearMonth = YearMonth.of(year, month);
        membership.setMonth(yearMonth);
        membership.setMembershipStartDate(startDate);
        membership.setMembershipEndDate(yearMonth.atEndOfMonth());
        membership.setDueDate(yearMonth.atEndOfMonth());
        membership.setMembershipPlan(MembershipPlan.MONTHLY);
        membership.setQuantity(noOfMemberships);
        return membership;
    }

    @Override
    public void save(Membership membership) {
        membershipRepository.save(membership);
    }

    @Override
    public List<MembershipType> getAllMemberships() {
        return membershipTypeRepository.findAll();
    }


    @Override
    public List<Membership> changeCurrentMembership(User user) {
        List<Membership> memberships = user.getMemberships();
        List<Membership> updatedMembership = new ArrayList<>();
        for (Membership currentMembership : memberships) {
            if (currentMembership.getMembershipPlan() == MembershipPlan.ANNUAL) {
                currentMembership.setMonth(currentMembership.getMonth().plusMonths(1));
                membershipRepository.save(currentMembership);
                updatedMembership.add(currentMembership);
            }
        }
        return updatedMembership;
    }

    @Override
    public List<AddOn> changeCurrentAddOns(User user) {
        List<AddOn> addOns = user.getAddOns();
        List<AddOn> updatedAddOns = new ArrayList<AddOn>();
        for (AddOn a : addOns) {
            if (a.getAddOnPlan() == AddOnPlan.ANNUAL) {
                a.setMonth(a.getMonth().plusMonths(1));
                addOnRepository.save(a);
                updatedAddOns.add(a);
            }
        }
        return updatedAddOns;
    }

//    @Override
//    public List<Membership> changeCurrentMembership(User user) {
//        log.debug("=================inside changeCurrentMembership=================");
//        List<Membership> memberships = user.getMemberships();
//        List<Membership> list = new ArrayList<>();
//        List<Membership> purchaseList = new ArrayList<>();
//        for (Membership currentMembership : memberships) {
//            Membership m = new Membership(currentMembership);
//            log.debug("inside for loop : membership plan = " + currentMembership.getMembershipPlan());
//            if (currentMembership.getMembershipPlan() == MembershipPlan.ANNUAL) {
//
//                log.debug("current month = " + currentMembership.getMonth());
//                m.setMonth(currentMembership.getMonth().plusMonths(1));
//                log.debug("new month = " + m.getMonth());
//                log.debug("old due date = " + m.getDueDate());
//                m.setDueDate(m.getMonth().plusMonths(1).atDay(5));
//                log.debug("new due date = " + m.getDueDate());
//                purchaseList.add(m);
//            }
////            } else if (currentMembership.getMembershipPlan() == MembershipPlan.MONTHLY) {
////
//////                currentMembership.setMonth(currentMembership.getMonth().plusMonths(1));
//////                currentMembership.setDueDate(currentMembership.getMonth().atEndOfMonth());
//////                currentMembership.setMembershipEndDate(currentMembership.getDueDate());
////            }
//            membershipRepository.save(m);
//            list.add(m);
//
//        }
//        user.setMemberships(list);
//        userRepository.save(user);
//        return purchaseList;
//    }

    //called via payNow
    @Override
    public List<Amount> getCurrentMembershipsPurchaseAmount(List<Membership> membership) {
        List<Amount> amountList = new ArrayList<>();
        for (Membership m : membership) {
            if (m.getMembershipPlan() == MembershipPlan.MONTHLY) continue;
            Amount amount = calculateAmount.calculateCurrentMembershipAmount(m);
            amountRepository.save(amount);
            amountList.add(amount);
        }
        return amountList;
    }

    @Override
    public List<Amount> getCurrentAddOnsPurchaseAmount(List<AddOn> addOns) {
        List<Amount> amountList = new ArrayList<>();
        for (AddOn a : addOns) {
            if (a.getAddOnPlan() == AddOnPlan.MONTHLY) {
                log.info("inside if add on plan is monthly");
                continue;
            }
            log.info("if addon plan in annual");
            Amount amount = calculateAmount.calculateCurrentAddOnAmount(a);
            amountRepository.save(amount);
            amountList.add(amount);
        }
        return amountList;
    }

//    @Override
//    public List<AddOn> changeCurrentAddOns(User user) {
//        List<AddOn> addOns = user.getAddOns();
//        List<AddOn> list = new ArrayList<>();
//        List<AddOn> purchaseList = new ArrayList<>();
//        for (AddOn a : addOns) {
//            AddOn addOn = new AddOn(a);
//            if (a.getAddOnPlan() == AddOnPlan.ANNUAL) {
//                addOn.setMonth(a.getMonth().plusMonths(1));
//                addOn.setDueDate(addOn.getMonth().plusMonths(1).atDay(5));
//                purchaseList.add(addOn);
//            }
////            else if (a.getAddOnPlan() == AddOnPlan.MONTHLY) {
////                continue;
//////                a.setMonth(a.getMonth().plusMonths(1));
//////                a.setDueDate(a.getMonth().atEndOfMonth());
//////                a.setEndDate(a.getDueDate());
////            }
//            addOnRepository.save(addOn);
//            list.add(addOn);
//        }
//        user.setAddOns(list);
//        userRepository.save(user);
//        return purchaseList;
//    }

    @Override
    public List<AddOn> createAddOns(List<AddOnDto> addOnList, LocalDate startDate,
                                    MembershipPlan membershipPlan, AnnualPaymentType annualPaymentType) {
        if (membershipPlan == MembershipPlan.ANNUAL) {
            return createAnnualAddOns(startDate, addOnList, annualPaymentType);
        }
        return createMonthlyAddOns(startDate, addOnList);


    }

    private List<AddOn> createMonthlyAddOns(LocalDate startDate, List<AddOnDto> addOnList) {
        List<AddOn> list = new ArrayList<>();
        for (AddOnDto dto : addOnList) {
            if (dto.getQuantity() < 1) continue;
            Optional<AddOnType> addOnTypeOptional = addOnTypeRepository.findById(dto.getId());
            AddOnType addOnType = addOnTypeOptional.get();
            AddOn addOn = new AddOn();
            addOn.setAddOnType(addOnType);
            addOn.setQuantity(dto.getQuantity());
            int year = startDate.getYear();
            int month = startDate.getMonthValue();
            YearMonth yearMonth = YearMonth.of(year, month);
            addOn.setMonth(yearMonth);
            addOn.setStartDate(startDate);
            addOn.setEndDate(yearMonth.atEndOfMonth());
            addOn.setDueDate(yearMonth.atEndOfMonth());
            addOn.setAddOnPlan(AddOnPlan.MONTHLY);
            addOnRepository.save(addOn);
            list.add(addOn);
        }
        return list;
    }

    private List<AddOn> createAnnualAddOns(LocalDate startDate, List<AddOnDto> addOnList,
                                           AnnualPaymentType annualPaymentType) {
        List<AddOn> list = new ArrayList<>();
        for (AddOnDto dto : addOnList) {
            if (dto.getQuantity() < 1) continue;
            Optional<AddOnType> addOnTypeOptional = addOnTypeRepository.findById(dto.getId());
            AddOnType addOnType = addOnTypeOptional.get();
            AddOn addOn = new AddOn();
            addOn.setAddOnType(addOnType);
            addOn.setQuantity(dto.getQuantity());
            int year = startDate.getYear();
            int month = startDate.getMonthValue();
            YearMonth yearMonth = YearMonth.of(year, month);
            addOn.setMonth(yearMonth);
            addOn.setStartDate(startDate);
            addOn.setEndDate(startDate.plusDays(365));
            if (annualPaymentType == AnnualPaymentType.ANNUAL) addOn.setDueDate(startDate.plusDays(365));
            else addOn.setDueDate(startDate.plusMonths(1).withDayOfMonth(5));
            addOn.setAddOnPlan(AddOnPlan.ANNUAL);
            addOn.setEndDate(yearMonth.atEndOfMonth());
            addOn.setDueDate(yearMonth.atEndOfMonth());
            addOn.setAddOnPlan(AddOnPlan.ANNUAL);
            addOnRepository.save(addOn);
            list.add(addOn);
        }
        return list;
    }


}
