package com.aquillius.portal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.aquillius.portal.enums.PaymentStatus;
import com.aquillius.portal.enums.PaymentType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String invoiceFilePath;
    private LocalDateTime invoiceDateTime;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private PaymentStatus invoiceStatus;
    private Float totalAmount;
    private String invoiceToken;
    private PaymentType paymentType;
    @OneToOne
    private Purchase purchase;



}
