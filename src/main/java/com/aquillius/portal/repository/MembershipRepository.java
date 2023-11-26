package com.aquillius.portal.repository;

import com.aquillius.portal.entity.Membership;
import com.aquillius.portal.entity.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Membership findByType(MembershipType type);
}
