package com.aquillius.portal.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.aquillius.portal.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aquillius.portal.entity.Notification;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.entity.VerificationToken;
import com.aquillius.portal.enums.UserRole;
import com.aquillius.portal.model.Message;
import com.aquillius.portal.service.UserService;
import com.aquillius.portal.service.VerificationTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/portal/verify")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class VerificationController {

    private final  UserService userService;

    private final  VerificationTokenService tokenService;

    private final NotificationRepository notificationRepository;
    
    @PostMapping("/email/{token}")
    public ResponseEntity<Message> verifyEmail(@PathVariable String token) {
    	
		log.info("===================inside verifyEmail API ===================");


        VerificationToken verificationToken = tokenService.findByToken(token);
        if (verificationToken == null) {
            return ResponseEntity.ok(new Message(0, "Invalid token"));
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userService.save(user);

        tokenService.delete(verificationToken);
        
        return ResponseEntity.ok(new Message(1, "Email verified Successfully"));

    }

    @GetMapping("/accept-admin/{token}")
    public ResponseEntity<Message> acceptAdmin(@PathVariable String token) {
    	
		log.info("===================inside acceptAdmin API ===================");

    	
        VerificationToken verificationToken = tokenService.findByToken(token);
        if (verificationToken == null) {
            return ResponseEntity.ok(new Message(0, "Invalid token"));
        }
        User user = verificationToken.getUser();
        user.setRole(UserRole.ADMIN);
        user.setIsAddedByAdminToEmployeeList(true);
//        userService.save(user);
        tokenService.delete(verificationToken);
        userService.sendAdminConfirmationMail(user);
        String message = "Your request for Admin role  has been accepted";
        Notification notification = new Notification(message, LocalDateTime.now());
        notificationRepository.save(notification);
        List<Notification> list = user.getNotifications();
        if (list == null) list=new ArrayList<>();
        list.add(notification);
        user.setNotifications(list);
        userService.save(user);
        return ResponseEntity.ok(new Message(1, "Admin accepted"));
    }


}

