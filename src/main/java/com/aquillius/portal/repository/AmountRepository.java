package com.aquillius.portal.repository;

import com.aquillius.portal.model.Amount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmountRepository extends JpaRepository<Amount, Long> {
}
