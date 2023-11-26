package com.aquillius.portal.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.aquillius.portal.dto.*;
import com.aquillius.portal.entity.ProfilePicture;
import com.aquillius.portal.enums.UserRole;
import com.aquillius.portal.repository.ProfilePictureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aquillius.portal.entity.Notification;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.model.Message;
import com.aquillius.portal.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/portal/user")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final ProfilePictureRepository profilePictureRepository;

    @PostMapping("/profile/picture/upload")
    public ResponseEntity<Message> uploadProfilePicture(@RequestBody FilePathDto dto, Principal principal) {

        log.info("===================inside uploadProfilePicture API ===================");

        try {
            User user = userService.findByEmail(principal.getName());
            userService.uploadProfilePicture(user, dto.filePath());
            return ResponseEntity.ok(new Message(1, "uploaded successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Message(0, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/profile/picture/download")
    public ResponseEntity<Message> getProfilePicture(Principal principal) {

        log.info("===================inside getProfilePicture API ===================");


        User user = userService.findByEmail(principal.getName());
        String profilePicture = userService.getProfilePicture(user);
        if (profilePicture == null) {
            return ResponseEntity.ok(new Message(0, "not present"));
        }
        return ResponseEntity.ok(new Message(1, profilePicture));
    }

    @PutMapping("/update/details")
    public ResponseEntity<Message> updateUserDetails(@RequestBody UserUpdateRequest request, Principal principal) {

        log.info("===================inside updateUserDetails API ===================");

        User user = userService.findByEmail(principal.getName());
        boolean updated = userService.updateUserDetails(request, user);
        if (updated)
            return ResponseEntity.ok(new Message(1, "User details updated successfully"));
        return ResponseEntity.ok(new Message(0, "Email already exists"));
    }

    @PutMapping("/update/password")
    public ResponseEntity<Message> changePassword(@RequestBody PasswordResetRequest request, Principal principal) {

        log.info("===================inside changePassword API ===================");

        User user = userService.findByEmail(principal.getName());
        if (!userService.verifyCurrentPassword(user, request.currentPassword())) {
            return ResponseEntity.ok(new Message(0, "Current password is wrong"));
        }
        userService.resetPassword(user, request.newPassword());
        return ResponseEntity.ok(new Message(1, "Password successfully reset"));
    }

    @GetMapping("/apply/admin")
    public ResponseEntity<Message> applyForAdmin(Principal principal) {

        log.info("===================inside applyForAdmin API ===================");


        User user = userService.findByEmail(principal.getName());
        log.debug("user name = " + user.getFirstName());
        if (user.getMemberships() == null || user.getMemberships().isEmpty()) {
            return ResponseEntity.ok(new Message(0, "Need to purchase membership to apply for Admin"));
        }
        userService.applyForAdmin(user);
        return ResponseEntity.ok(new Message(1, "Applied, will be notified once accepted"));
    }

    @GetMapping("/check-admin")
    public ResponseEntity<Message> checkAdmin(Principal principal) {

        log.info("===================inside checkAdmin API ===================");
        User user = userService.findByEmail(principal.getName());
        if (user.getRole() == UserRole.ADMIN) {
            return ResponseEntity.ok(new Message(1, "Is admin"));
        }
        return ResponseEntity.ok(new Message(0, "Not an Admin"));
    }


    @GetMapping("/membership-page")
    public ResponseEntity<MembershipPageDto> getMembershipPageData(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        MembershipPageDto membershipPageDto = userService.getMembershipPageData(user);
        return ResponseEntity.ok(membershipPageDto);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotifications(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Notification> list = user.getNotifications();
//        if (list == null) {
//            List<Notification> newList = new ArrayList<>();
//            Notification notification = new Notification("demo notification", LocalDateTime.now());
//            newList.add(notification);
//            return ResponseEntity.ok(newList);
//        }
        if (list == null || list.isEmpty()) {
            return ResponseEntity.ok(list);
        }
        Comparator<Notification> timestampComparator = (n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp());
        list.sort(timestampComparator);
        user.setNotifications(new ArrayList<>());
        userService.save(user);
//        if (list.isEmpty()) {
//            Notification notification = new Notification("demo notification", LocalDateTime.now());
//            list.add(notification);
//        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/profile/billing-info")
    public ResponseEntity<BillingInfoDto> getBillingInfo(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        BillingInfoDto billingInfoDto = userService.getBillingInfo(user);
        return ResponseEntity.ok(billingInfoDto);
    }

    @GetMapping("/profile/contact-details")
    public ResponseEntity<ContactDetailsDto> getContactDetails(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        ContactDetailsDto contactDetailsDto = userService.getContactDetails(user);
        return ResponseEntity.ok(contactDetailsDto);
    }

    @PostMapping("/profile/upload/billing-info")
    public ResponseEntity<Message> uploadBillingInfo(@RequestBody BillingInfoDto dto, Principal principal) {
        log.info("====================inside upload billing info api===========================");
        User user = userService.findByEmail(principal.getName());
        userService.uploadBillingInfo(user, dto);
        return ResponseEntity.ok(new Message(1, "uploaded successfully"));
    }

    @PostMapping("/profile/upload/contact-details")
    public ResponseEntity<Message> uploadContactDetails(@RequestBody ContactDetailsDto dto, Principal principal) {
        log.info("====================inside upload contact details api===========================");
        User user = userService.findByEmail(principal.getName());
        userService.uploadContactDetails(user, dto);
        return ResponseEntity.ok(new Message(1, "uploaded successfully"));
    }

    @GetMapping("/profile/billing-address-check")
    public ResponseEntity<Message> billingAddressExist(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        boolean billingAddressPresent = userService.billingAddressExist(user);
        if (billingAddressPresent) return ResponseEntity.ok(new Message(1, "billing address set"));
        return ResponseEntity.ok(new Message(0, "billing address not set"));
    }

    @GetMapping("/has-membership")
    public ResponseEntity<Message> checkMembership(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        boolean hasMembership = userService.checkMembership(user);
        if (hasMembership) return ResponseEntity.ok(new Message(1, "has membership"));
        return ResponseEntity.ok(new Message(0, "does not have membership"));
    }

}
