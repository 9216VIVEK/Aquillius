package com.aquillius.portal.entity;

import com.aquillius.portal.enums.UserRole;

import jakarta.persistence.*;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String profilePicture;
    private String firstName;
    private String lastName;
    @ManyToOne
    @JoinColumn(name="company_id")
    private Company company;
    private String phoneNumber;
    @Column(nullable = false, unique = true)
    private String email;
    private boolean emailVerified;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @OneToMany
    private List<Membership> memberships;
    @OneToMany
    private List<AddOn> addOns;
    @OneToMany
    private List<Purchase> listOfPurchases;
    @OneToMany
    private List<Notification> notifications;
    @OneToOne
    @JoinColumn(name = "billingInfo_id")
    private BillingInfo billingInfo;
    @OneToOne
    @JoinColumn(name = "contactDetails_id")
    private ContactDetails contactDetails;
    @Column(name = "is_added_by_admin_to_employee_list", nullable = false, columnDefinition = "boolean default true")
    private Boolean isAddedByAdminToEmployeeList;

}
