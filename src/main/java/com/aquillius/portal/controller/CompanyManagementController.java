package com.aquillius.portal.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.aquillius.portal.dto.FilePathDto;
import com.aquillius.portal.entity.CompanyLogo;
import com.aquillius.portal.entity.ProfilePicture;
import com.aquillius.portal.repository.CompanyLogoRepository;
import com.aquillius.portal.repository.CompanyRepository;
import com.aquillius.portal.repository.UserRepository;
import com.aquillius.portal.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aquillius.portal.dto.AddEmployeeDto;
import com.aquillius.portal.dto.EmployeeDto;
import com.aquillius.portal.entity.Company;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.enums.UserRole;
import com.aquillius.portal.model.Message;
import com.aquillius.portal.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/portal/company")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class CompanyManagementController {

    private final UserService userService;

    private final UserRepository userRepository;

    private final CompanyLogoRepository companyLogoRepository;

    private final CompanyService companyService;

    private final CompanyRepository companyRepository;


    @PostMapping("/add-employee")
    public ResponseEntity<Message> addEmployee(@RequestBody AddEmployeeDto dto, Principal principal) {

        log.info("===================inside addEmployee API ===================");

        try {
            // check employee is admin
            User user = userService.findByEmail(principal.getName());
            if (!user.getRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.ok(new Message(0, "User not authorised to add employee"));
            }
            userService.addEmployee(dto, user);
            return ResponseEntity.ok(new Message(1, "Employee added successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new Message(0, "Something went Wrong"));
        }
    }

    @PutMapping("/remove-employee/{id}")
    public ResponseEntity<Message> removeEmployee(@PathVariable long id, Principal principal) {

        log.info("===================inside removeEmployee API ===================");

        try {
            // check employee is admin
            User user = userService.findByEmail(principal.getName());
            if (!user.getRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.ok(new Message(0, "User not authorised to remove employee"));
            }
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                return ResponseEntity.ok(new Message(1, "Employee removed successfully"));
            }
            User userToBeRemoved = userOptional.get();
            if (user.equals(userToBeRemoved)) {
                return ResponseEntity.ok(new Message(0, "Cannot remove self"));
            }
            userService.removeEmployee(userToBeRemoved);
            return ResponseEntity.ok(new Message(1, "Employee removed successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new Message(0, "Something went Wrong"));
        }
    }

    @PostMapping("/add-logo")
    public ResponseEntity<Message> addCompanyLogo(@RequestBody FilePathDto dto, Principal principal) {

        log.info("===================inside removeEmployee API ===================");

        try {
            // check employee is admin
            User user = userService.findByEmail(principal.getName());
            if (!user.getRole().equals(UserRole.ADMIN)) {
                return ResponseEntity.ok(new Message(0, "User not authorised to add logo"));
            }
            companyService.addLogo(dto.filePath(), user.getCompany());
            return ResponseEntity.ok(new Message(1, "Logo added successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new Message(0, "Something went Wrong"));
        }
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDto>> getCompanyEmployees(Principal principal) {

        log.info("===================inside getCompanyEmployees API ===================");

        User user = userService.findByEmail(principal.getName());

        Company company = user.getCompany();
        List<User> employees = company.getListOfEmployees();
        if (employees.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<EmployeeDto> list = new ArrayList<>();
        for (User u : employees) {
            if (u.getIsAddedByAdminToEmployeeList()) {
                EmployeeDto dto = new EmployeeDto();
                dto.setId(u.getId());
                dto.setFirstName(u.getFirstName());
                dto.setLastName(u.getLastName());
                dto.setRole(u.getRole());
                String profilePic = u.getProfilePicture();
                if (profilePic == null) profilePic = "0";
                dto.setProfilePic(profilePic);
                list.add(dto);
            }
        }
        return ResponseEntity.ok(list);
    }


}
