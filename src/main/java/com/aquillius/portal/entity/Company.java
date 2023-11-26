package com.aquillius.portal.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String logo;
    private String name;
    @OneToMany(mappedBy = "company")
    private List<User> listOfEmployees;


}
