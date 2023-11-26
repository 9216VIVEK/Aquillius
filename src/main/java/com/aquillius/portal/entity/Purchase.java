package com.aquillius.portal.entity;


import com.aquillius.portal.enums.PaymentStatus;
import com.aquillius.portal.enums.PurchasableItem;
import com.aquillius.portal.model.Amount;

import com.aquillius.portal.model.FinalAmount;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Data
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    PurchasableItem purchasableItem;
    @OneToMany
    private List<Membership> memberships;
    @OneToMany
    private List<AddOn> addOns;
    private LocalDateTime purchaseDateTime;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @OneToOne
    private FinalAmount amount;

}
