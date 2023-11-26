package com.aquillius.portal.service.serviceImpl;

import com.aquillius.portal.entity.Company;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.repository.CompanyRepository;
import com.aquillius.portal.repository.UserRepository;
import com.aquillius.portal.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    public void save(Company company) {
        companyRepository.save(company);
    }

    public List<User> findListOfUsersByCompany(Company company) {
        return companyRepository.findListOfUsersByName(company.getName());
    }

    @Override
    public Company findByName(String companyName) {
        return companyRepository.findByName(companyName);
    }

    @Override
    public void addLogo(String logo, Company company) {
        company.setLogo(logo);
        companyRepository.save(company);
    }
}
