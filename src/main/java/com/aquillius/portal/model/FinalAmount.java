package com.aquillius.portal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FinalAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    List<Amount> amountList;

    float totalAmount;

    int initialPurchase; //1 for true, 0 for false

    public FinalAmount(List<Amount> amountList, float totalAmount) {
        this.amountList = amountList;
        this.totalAmount = totalAmount;
    }
}
