package com.aquillius.portal.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import com.aquillius.portal.dto.MembershipAndAddOnResponse;
import com.aquillius.portal.dto.PlansDto;
import com.aquillius.portal.entity.Membership;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.repository.AddOnTypeRepository;
import com.aquillius.portal.repository.MembershipTypeRepository;
import com.aquillius.portal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aquillius.portal.entity.MembershipType;
import com.aquillius.portal.model.Message;
import com.aquillius.portal.service.MembershipService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/portal/membership")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    private final MembershipTypeRepository membershipTypeRepository;

    private final UserService userService;

    private final AddOnTypeRepository addOnTypeRepository;

//    @GetMapping("/list-memberships")
//    public ResponseEntity<?> getAllMemberships(Principal principal) {
//        try {
//            List<MembershipType> list = membershipService.getAllMemberships();
//            User user = userService.findByEmail(principal.getName());
//            Membership membership = user.getMembership();
//            List<PlansDto> listOfPlans = new ArrayList<>();
//            list.forEach(membershipType -> {
//                PlansDto dto=new PlansDto();
//                dto.setId(membershipType.getId());
//                dto.setName(membershipType.getName());
//                dto.setAnnualPrice(membershipType.getAnnualPrice());
//                dto.setMonthlyPrice(membershipType.getMonthlyPrice());
//                if (membership != null && membershipType == membership.getType()) dto.setCurrentMembership(1);
//                else dto.setCurrentMembership(0);
//                listOfPlans.add(dto);
//            });
//            return ResponseEntity.ok(listOfPlans);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.ok(new Message(0, "Something went Wrong"));
//        }
//    }


    @GetMapping("/list-memberships")
    public ResponseEntity<?> getAllMemberships(Principal principal) {
        try {
            List<MembershipType> list = membershipService.getAllMemberships();
            User user = userService.findByEmail(principal.getName());
            Membership membership = user.getMemberships() == null || user.getMemberships().isEmpty() ? null :
                    user.getMemberships().get(user.getMemberships().size() - 1);
            List<PlansDto> listOfPlans = new ArrayList<>();
            PlansDto currentPlanDto = null;
            for (MembershipType membershipType : list) {
                PlansDto dto = new PlansDto();
                dto.setId(membershipType.getId());
                dto.setName(membershipType.getName());
                dto.setAnnualPrice(membershipType.getAnnualPrice());
                dto.setMonthlyPrice(membershipType.getMonthlyPrice());
                dto.setColor(membershipType.getColor());
                List<String> featureList = new ArrayList<>();
                if (membershipType.getLine1() != null) featureList.add(membershipType.getLine1());
                if (membershipType.getLine2() != null) featureList.add(membershipType.getLine2());
                if (membershipType.getLine3() != null) featureList.add(membershipType.getLine3());
                if (membershipType.getLine4() != null) featureList.add(membershipType.getLine4());
                if (membershipType.getLine5() != null) featureList.add(membershipType.getLine5());
                if (membershipType.getLine6() != null) featureList.add(membershipType.getLine6());
                if (membershipType.getLine7() != null) featureList.add(membershipType.getLine7());
                dto.setLines(featureList);
//                if (membership != null && membershipType == membership.getType()) {
//                    dto.setCurrentMembership(1);
//                    currentPlanDto = dto;
//                } else {
//                    dto.setCurrentMembership(0);
//                    listOfPlans.add(dto);
//                }
                if (membership != null && membershipType == membership.getType()) {
                    dto.setCurrentMembership(1);
                    currentPlanDto = dto;
                } else {
                    dto.setCurrentMembership(0);

                }
                listOfPlans.add(dto);
            }
//            if (currentPlanDto != null) listOfPlans.add(0, currentPlanDto);
            return ResponseEntity.ok(listOfPlans);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new Message(0, "Something went Wrong"));
        }
    }

    @GetMapping("/get-membership-data-and-addOns/{id}")
    public ResponseEntity<?> getMembershipData(@PathVariable int id) {
        MembershipAndAddOnResponse response = new MembershipAndAddOnResponse();
        MembershipType type = membershipTypeRepository.findById(id);

        PlansDto dto = new PlansDto();
        dto.setId(type.getId());
        dto.setName(type.getName());
        dto.setAnnualPrice(type.getAnnualPrice());
        dto.setMonthlyPrice(type.getMonthlyPrice());
        dto.setColor(type.getColor());
        dto.setAddOn(type.getAddOn());
        List<String> featureList = new ArrayList<>();
        if (type.getLine1() != null) featureList.add(type.getLine1());
        if (type.getLine2() != null) featureList.add(type.getLine2());
        if (type.getLine3() != null) featureList.add(type.getLine3());
        if (type.getLine4() != null) featureList.add(type.getLine4());
        if (type.getLine5() != null) featureList.add(type.getLine5());
        if (type.getLine6() != null) featureList.add(type.getLine6());
        if (type.getLine7() != null) featureList.add(type.getLine7());
        dto.setLines(featureList);
        response.setPlansDto(dto);
        response.setAddOnTypes(addOnTypeRepository.findAll());
        return ResponseEntity.ok(response);
    }


}
