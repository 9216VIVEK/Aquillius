package com.aquillius.portal.dto;

import com.aquillius.portal.entity.MembershipType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlansDto {

    private long id;

    private String name;

    private float monthlyPrice;

    private float annualPrice;

    private int addOn;

    //1 if true i.e. this is the current membership type of the user, 0 if false
    int currentMembership;

    private String color;

    private List<String> lines;

}
