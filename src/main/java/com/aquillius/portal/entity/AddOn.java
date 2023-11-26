package com.aquillius.portal.entity;

import com.aquillius.portal.enums.AddOnPlan;
import com.aquillius.portal.enums.MembershipPlan;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Data
@NoArgsConstructor
public class AddOn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    AddOnType addOnType;

    Integer quantity;

    @ManyToOne
    private User user;
    private YearMonth month;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private AddOnPlan addOnPlan;

    public AddOn(AddOn a){
        this.addOnType=a.getAddOnType();
        this.user=a.getUser();
        this.quantity=a.getQuantity();
        this.month=a.getMonth();
        this.startDate=a.getStartDate();
        this.endDate=a.getEndDate();
        this.dueDate=a.getDueDate();
        this.addOnPlan=a.getAddOnPlan();
    }

}
