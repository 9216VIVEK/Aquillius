package com.aquillius.portal.service;

import com.aquillius.portal.dto.AddOnDto;
import com.aquillius.portal.dto.MembershipPurchaseDto;
import com.aquillius.portal.entity.AddOn;
import com.aquillius.portal.entity.Membership;
import com.aquillius.portal.entity.MembershipType;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.enums.MembershipPlan;
import com.aquillius.portal.enums.AnnualPaymentType;
import com.aquillius.portal.model.Amount;
import com.aquillius.portal.model.FinalAmount;

import java.time.LocalDate;
import java.util.List;

public interface MembershipService {

    FinalAmount getMembershipPurchaseAmount(MembershipPurchaseDto membershipPurchaseDto, User user) throws Exception;

    Membership createMembership(MembershipType membershipType, MembershipPlan membershipPlan,
                                AnnualPaymentType annualPaymentType, User user, LocalDate startDate, List<AddOnDto> addOnList,
                                int noOfMemberships) throws Exception;

    void save(Membership membership) throws Exception;

    List<MembershipType> getAllMemberships() throws Exception;

    List<Membership> changeCurrentMembership(User user);

    List<Amount> getCurrentMembershipsPurchaseAmount(List<Membership> membership);

    List<Amount> getCurrentAddOnsPurchaseAmount(List<AddOn> addOns);

    List<AddOn> changeCurrentAddOns(User user);

    List<AddOn> createAddOns(List<AddOnDto> addOnList, LocalDate startDate,
                             MembershipPlan membershipPlan, AnnualPaymentType annualPaymentType);
}
