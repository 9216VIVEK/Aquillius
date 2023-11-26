package com.aquillius.portal.repository;

import com.aquillius.portal.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

}
