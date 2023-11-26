package com.aquillius.portal.service;

import com.aquillius.portal.entity.Company;
import com.aquillius.portal.entity.User;

import java.util.List;

public interface CompanyService {

    void save(Company company);

    List<User> findListOfUsersByCompany(Company company);

    Company findByName(String companyName);

    void addLogo(String logo, Company company);
}
