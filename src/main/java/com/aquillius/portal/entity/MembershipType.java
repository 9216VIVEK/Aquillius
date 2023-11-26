package com.aquillius.portal.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MembershipType {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    private String name;

    private Float monthlyPrice;

    private Float annualPrice;

    //-1 for virtual, 0 for base, 1 for office
    private Integer addOn;

    private String color;

    private String line1;

    private String line2;

    private String line3;

    private String line4;

    private String line5;

    private String line6;

    private String line7;

}
