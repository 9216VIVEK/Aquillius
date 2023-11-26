package com.aquillius.portal.dto;

import com.aquillius.portal.entity.AddOnType;

import java.time.LocalDate;
import java.util.List;


public record MembershipPurchaseDto

    (long membershipTypeId,

//1 - monthly , 2 - annual
     long membershipPlan,
//1 - monthly, 2 - annual
     long membershipAnnualPaymentType,

     LocalDate startDate,
     //1 if from pay now else 0 if first purchase
     int payNow,

     List<AddOnDto> addOnList,

    int noOfMemberships)


{
    @Override
    public String toString() {
        return "MembershipPurchaseDto{" +
                "membershipTypeId=" + membershipTypeId +
                ", membershipPlan=" + membershipPlan +
                ", membershipAnnualPaymentType=" + membershipAnnualPaymentType +
                ", startDate=" + startDate +
                ", payNow=" + payNow +
                ", addOnList=" + addOnList +
                '}';
    }
}
