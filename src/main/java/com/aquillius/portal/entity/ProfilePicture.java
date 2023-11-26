package com.aquillius.portal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProfilePicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] image;
}
