package com.aquillius.portal.entity;

import com.aquillius.portal.enums.MembershipPlan;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private MembershipType type;
    @ManyToOne
    private User user;
    private int quantity;
    private YearMonth month;
    private LocalDate membershipStartDate;
    private LocalDate membershipEndDate;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private MembershipPlan membershipPlan;

    public Membership(Membership m){
        this.type=m.getType();
        this.user=m.getUser();
        this.quantity=m.getQuantity();
        this.month=m.getMonth();
        this.membershipStartDate=m.getMembershipStartDate();
        this.membershipEndDate=m.getMembershipEndDate();
        this.dueDate=m.getDueDate();
        this.membershipPlan=m.getMembershipPlan();
    }



}

