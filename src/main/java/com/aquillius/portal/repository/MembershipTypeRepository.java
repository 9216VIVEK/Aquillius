package com.aquillius.portal.repository;

import com.aquillius.portal.entity.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipTypeRepository extends JpaRepository<MembershipType, Long> {

    MembershipType findByName(String membership);

    MembershipType findById(long id);

}
