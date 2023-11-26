package com.aquillius.portal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Notification implements Comparable<Notification>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private LocalDateTime timestamp;

    public Notification(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

	@Override
	public int compareTo(Notification o) {
		return this.timestamp.compareTo(o.timestamp);
	}
}
