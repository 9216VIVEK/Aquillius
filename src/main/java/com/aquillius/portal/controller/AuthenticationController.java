package com.aquillius.portal.controller;

import com.aquillius.portal.model.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aquillius.portal.dto.SignUpRequest;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.model.JwtRequest;
import com.aquillius.portal.model.Message;
import com.aquillius.portal.service.UserService;
import com.aquillius.portal.util.JWTUtility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/portal/authenticate")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JWTUtility jwtUtility;

    @PostMapping("/signup")
    public ResponseEntity<Message> signup(@RequestBody SignUpRequest signupRequest) {

        log.info("===================inside signup API ===================");
        // Check if the user already exists in the database/
        boolean exists=userService.existsByEmail(signupRequest.email());
        log.info("exists = " + exists);
        if (exists) {
            log.info("===inside if in signup api i.e. user already exists====");
            return ResponseEntity.ok(new Message(0, "user with email already exists"));
        }

        try {

            userService.createUser(signupRequest);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new Message(0, "Something went Wrong"));
        }
        return ResponseEntity.ok(new Message(1, signupRequest.email()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest jwtRequest) {

        log.info("===================inside login API ===================");

        try {
            log.info("inside try");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));


            User user = userService.findByEmail(jwtRequest.getUsername());
            if (!user.isEmailVerified()) {
                log.info("inside if");
                 return ResponseEntity.ok(new Message(0, "email not verified"));
            }


            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtility.generateJwtToken(authentication);
            boolean hasMembership = userService.checkMembership(user);
            int code = hasMembership ? 1 : 2;
            return ResponseEntity.ok(new Message(code, token));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new Message(0, "Invalid Credentials"));
        }

    }

    @PutMapping("/forgot-password/{email}")
    public ResponseEntity<Message> forgetPassword(@PathVariable String email) {

        log.info("===================inside forgetPassword API ===================");
        try {

            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.ok(new Message(0, "user does not exist"));
            }

            userService.forgetPassword(email);

            return ResponseEntity.ok(new Message(1, "New password sent on mail"));

        } catch (Exception e) {
            e.printStackTrace();
            return  ResponseEntity.ok(new Message(0, "Something went Wrong"));
        }
    }
}