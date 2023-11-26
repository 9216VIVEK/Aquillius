package com.aquillius.portal.dto;

import java.util.List;

import com.aquillius.portal.entity.AddOn;
import com.aquillius.portal.model.PurchaseMembershipHistory;

import lombok.Data;

@Data
public class MembershipPageDto {

    String profilePic;

    String email;

    String firstName;

    String lastName;

    String companyName;

    String companyLogo;

    String currentMembershipType;

    String membershipDueDate;

    String role;

    boolean isEligibleForAdmin;

    List<PurchaseMembershipHistory> purchaseMembershipHistory;

    float upcomingPayment;

    String membershipStartDate;

    String membershipEndDate;
//2 annual, 1 monthly, 0 no membership
    int membershipPlan;

    List<AddOn> addOns;

}
