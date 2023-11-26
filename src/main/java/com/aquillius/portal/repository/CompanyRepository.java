package com.aquillius.portal.repository;

import com.aquillius.portal.entity.Company;
import com.aquillius.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<User> findListOfUsersByName(String name);

    Company findByName(String companyName);

}
