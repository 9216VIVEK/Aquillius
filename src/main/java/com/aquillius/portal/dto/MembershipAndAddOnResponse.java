package com.aquillius.portal.dto;

import com.aquillius.portal.entity.AddOnType;
import com.aquillius.portal.entity.MembershipType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembershipAndAddOnResponse {

    PlansDto plansDto;

    List<AddOnType> addOnTypes;

}
