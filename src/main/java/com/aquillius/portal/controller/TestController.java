package com.aquillius.portal.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aquillius.portal.entity.Membership;
import com.aquillius.portal.entity.Purchase;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.enums.PaymentStatus;
import com.aquillius.portal.enums.PurchasableItem;
import com.aquillius.portal.model.Amount;
import com.aquillius.portal.model.JwtRequest;
import com.aquillius.portal.model.Message;
import com.aquillius.portal.repository.MembershipRepository;
import com.aquillius.portal.repository.PurchaseRepository;
import com.aquillius.portal.service.MembershipService;
import com.aquillius.portal.service.UserService;
import com.aquillius.portal.util.JWTUtility;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    UserService userService;

    @Autowired
    MembershipService membershipService;

//    @GetMapping("/getNamePrincipal")
//    public ResponseEntity<String> getNamePrincipal(Principal principal) {
//        User user= userService.findByEmail(principal.getName());
//        user.setMembership(membershipService.findByType(MembershipType.NONE));
//        userService.save(user);
//        System.out.println("user saved");
//        return ResponseEntity.ok("Principal name : " + principal.getName());
//    }

    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    JWTUtility jwtUtility;
    
    @Autowired
    AuthenticationManager authenticationManager;

    @GetMapping("/jwtCheck/{email}")
    public ResponseEntity<String> getNameStatic(@PathVariable String email) {
        User user=userService.findByEmail(email);
        JwtRequest jwtRequest=new JwtRequest();
        jwtRequest.setUsername(user.getEmail());
        jwtRequest.setPassword(user.getPassword());
        System.out.println(user.getPassword());

        UserDetails userDetails
                = userDetailsService.loadUserByUsername(jwtRequest.getUsername());



        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtUtility.generateJwtToken(authentication);
        
        return ResponseEntity.ok(jwtToken);
    }

//    @GetMapping("/print")
//    public ResponseEntity<Message> print() {
//        return ResponseEntity.ok(new Message("Kartikeya"));
//    }


    @Autowired
    PurchaseRepository purchaseRepository;
    @Autowired
    MembershipRepository membershipRepository;

//    @GetMapping("/getMembership")
//    public MembershipType getMembership(Principal principal) {
//        System.out.println("aaaa");
//        System.out.println(MembershipType.NONE);
//        Membership membership=membershipRepository.findByType(MembershipType.NONE);
//        return membership.getType();
//    }

    @GetMapping("/getPurchases")
    public List<Purchase> getPurchases(Principal principal) {
        User user=userService.findByEmail(principal.getName());
        return user.getListOfPurchases();
    }

//    @PostMapping("/addPurchase")
//    public ResponseEntity<Message> addPurchases(Principal principal) {
//        User user=userService.findByEmail(principal.getName());
//        Purchase p=new Purchase();
//        p.setPurchasableItem(PurchasableItem.MEMBERSHIP);
//        Amount a=new Amount();
//        a.setLateFee(0);
//        a.setAmount(600);
//        a.setTotalAmount(600);
//        p.setAmount(a);
//        p.setMembership(user.getMembership());
//        p.setPurchaseDateTime(LocalDateTime.now());
//        p.setPaymentStatus(PaymentStatus.SUCCESS);
//        purchaseRepository.save(p);
//        user.getListOfPurchases().add(p);
//        userService.save(user);
//        return ResponseEntity.ok(new Message(1, "Added"));
//    }


//    @PostMapping("/addMembership")
//    public ResponseEntity<Message> addMembership(Principal principal) {
//        User user=userService.findByEmail(principal.getName());
//        Membership membership=membershipService.createMembership(MembershipType.BASE, user);
//        membershipRepository.save(membership);
//        user.setMembership(membership);
//        userService.save(user);
//        return ResponseEntity.ok(new Message(1, "Added"));
//    }

//    @GetMapping("/getMembership")
//    public ResponseEntity<Membership> getMembership(Principal principal) {
//        User user=userService.findByEmail(principal.getName());
//        return ResponseEntity.ok(user.getMembership());
//    }
}
