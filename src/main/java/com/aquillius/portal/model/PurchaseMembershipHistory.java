package com.aquillius.portal.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PurchaseMembershipHistory {

    String membershipType;

    LocalDateTime dateTime;

    float price;

}
