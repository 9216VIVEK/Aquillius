package com.aquillius.portal.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Amount {

    //if initial purchase then show :- item, price, quantity, total price, total amount//rename it to prorated price
    // else                    show item, price, quantity, total price, late fee, total amount

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String item;

    private float price;

    private int quantity;

    private float totalPrice;

    private float totalAmount;

    private float amount;

    private float lateFee;

    public Amount(String item, float price, float totalPrice, int quantity,
                  float totalAmount, float amount, float lateFee) {
        this.item = item;
        this.price = price;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.amount = amount;
        this.lateFee = lateFee;
    }
}
