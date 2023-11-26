package com.aquillius.portal.service.serviceImpl;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.aquillius.portal.entity.*;
import com.aquillius.portal.enums.*;
import com.aquillius.portal.model.FinalAmount;
import com.aquillius.portal.model.MembershipAmountResponse;
import com.aquillius.portal.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aquillius.portal.dto.AddEmployeeDto;
import com.aquillius.portal.dto.BillingInfoDto;
import com.aquillius.portal.dto.ContactDetailsDto;
import com.aquillius.portal.dto.MembershipPageDto;
import com.aquillius.portal.dto.SignUpRequest;
import com.aquillius.portal.dto.UserUpdateRequest;
import com.aquillius.portal.model.Amount;
import com.aquillius.portal.model.PurchaseMembershipHistory;
import com.aquillius.portal.repository.BillingInfoRepository;
import com.aquillius.portal.repository.ContactDetailsRepository;
import com.aquillius.portal.repository.UserRepository;
import com.aquillius.portal.util.CalculateAmount;
import com.aquillius.portal.util.PasswordGenerator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final CompanyService companyService;

    private final EmailService emailService;

    private final VerificationTokenService tokenService;

    private final CalculateAmount calculateAmount;

    private final BillingInfoRepository billingInfoRepository;

    private final ContactDetailsRepository contactDetailsRepository;

    private final MembershipService membershipService;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void uploadProfilePicture(User user, String file) throws Exception {
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        user.setProfilePicture(file);
        save(user);
    }

    public String getProfilePicture(User user) {
        return user.getProfilePicture();
    }


    @Transactional
    public User createUser(SignUpRequest signupRequest) throws Exception {
        // Create a new user and store details in the database
        User user = new User();
        user.setEmail(signupRequest.email().toLowerCase());
        user.setFirstName(signupRequest.firstName());
        user.setLastName(signupRequest.lastName());
        user.setPassword(passwordEncoder.encode(signupRequest.password()));
        user.setPhoneNumber(signupRequest.phoneNumber());
        String companyName = signupRequest.companyName();
        if (companyName != null) {
            Company company = companyService.findByName(companyName.toUpperCase());
            if (company == null) {
                company = new Company();
                company.setName(companyName.toUpperCase());
                companyService.save(company);
            }
            user.setCompany(company);
            if (company.getListOfEmployees() != null) {
                company.getListOfEmployees().add(user);
            } else {
                List<User> employeeList = new ArrayList<>();
                employeeList.add(user);
                company.setListOfEmployees(employeeList);
            }
            companyService.save(company);
        }
        user.setIsAddedByAdminToEmployeeList(false);
        save(user);
        emailService.sendVerificationCode(user);
        return user;
    }

    public boolean updateUserDetails(UserUpdateRequest dto, User user) {
        if (dto.firstName() != null) user.setFirstName(dto.firstName());
        if (dto.lastName() != null) user.setLastName(dto.lastName());
//        if (user.getEmail().equals(dto.getEmail())) {
//            save(user);
//            return true;
//        }
//        if (existsByEmail(dto.getEmail())) {
//            return false;
//        }
//        user.setEmail(dto.getEmail());
        save(user);
        return true;
    }

    public void resetPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        save(user);
    }

    public boolean verifyCurrentPassword(User user, String currentPassword) {
        String password = user.getPassword();
        return passwordEncoder.matches(currentPassword, password);
    }

    public void addEmployee(AddEmployeeDto dto, User loggedInUser) {
        Company company = loggedInUser.getCompany();
        String randomPassword = null;
        String firstName = dto.firstName();
        String lastName = dto.lastName();

        if (userRepository.existsByEmail(dto.email())) {
            User newEmployee = userRepository.findByEmail(dto.email());
            company.getListOfEmployees().add(newEmployee);
            newEmployee.setCompany(company);
            newEmployee.setIsAddedByAdminToEmployeeList(true);
            save(newEmployee);
            companyService.save(company);
            String content = "You have been added to " + company.getName() + " company on Aquillius Portal";
            String subject = "Aquillius Portal";
            emailService.sendEmail(newEmployee.getEmail(), subject, content);
        } else {
            User user = new User();
            user.setEmail(dto.email());
            user.setCompany(loggedInUser.getCompany());
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmailVerified(true);
            company.getListOfEmployees().add(user);
            randomPassword = PasswordGenerator.generatePassword(8);
            user.setPassword(passwordEncoder.encode(randomPassword));
            user.setIsAddedByAdminToEmployeeList(true);
            save(user);
            companyService.save(company);
            String content = "You have been added to " + company.getName() +
                    " company on Aquillius Portal. Password : " + randomPassword;
            String subject = "Aquillius Portal";
            emailService.sendEmail(user.getEmail(), subject, content);
        }
    }

    private String fetchLastName(String name) {
        int i = 0;
        while (name.charAt(i) != ' ') i++;
        return name.substring(i + 1);
    }

    private String fetchFirstName(String name) {
        int i = 0;
        while (name.charAt(i) != ' ') i++;
        return name.substring(0, i);
    }

    @Transactional
    public void forgetPassword(String email) throws Exception {
        User user = findByEmail(email);
        String password = PasswordGenerator.generatePassword(8);
        user.setPassword(passwordEncoder.encode(password));
        save(user);
        String emailContent = "New password is : " + password;
        emailService.sendEmail(user.getEmail(), "Password Reset", emailContent);
    }

    @Override
    public void applyForAdmin(User user) {
        log.info("==============inside apply for admin UserServiceImpl=============");
        VerificationToken token = tokenService.createVerificationToken(user);
        log.debug("Verification token = " + token.getToken());
        String link = "https://e3a9941a87cc159c.ngrok.app/website/admin_token.php?token=" + token.getToken();
        String emailContent = "The following user wants to apply for Admin role :- " +
                "\nFirst Name : " + user.getFirstName() +
                "\nLast Name : " + user.getLastName() +
                "\nEmail : " + user.getEmail() +
                "\nCompany : " + user.getCompany().getName() +
                "\nClick on the following link to allow :- " + link;
        String email = "kartikeya.maan1997@gmail.com";
        System.out.println("3333333333333333333333");
        emailService.sendEmail(email, "Admin Application Request", emailContent);
    }

//    @Override
//    public List<PurchaseMembershipHistory> getPurchaseMembershipHistory(User user) {
//        List<Purchase> listOfPurchases = user.getListOfPurchases();
//        List<PurchaseMembershipHistory> history = new ArrayList<>();
//        listOfPurchases
//                .stream()
//                .filter(p -> p.getPurchasableItem().equals(PurchasableItem.MEMBERSHIP) &&
//                        p.getPaymentStatus().equals(PaymentStatus.SUCCESS))
//                .forEach(p -> {
//                    PurchaseMembershipHistory purchaseHistory = new PurchaseMembershipHistory();
//                    purchaseHistory.setDateTime(p.getPurchaseDateTime());
//                    purchaseHistory.setPrice(p.getAmount().getTotalAmount());
//                    purchaseHistory.setMembershipType(p.getMembership().getType().getName());
//                    history.add(purchaseHistory);
//                });
//        Collections.sort(history, Comparator.comparing(PurchaseMembershipHistory::getDateTime).reversed());
//        List<PurchaseMembershipHistory> historyTopFew = new ArrayList<>();
//        int i = 0;
//        while (i < 12 && i < history.size()) {
//            historyTopFew.add(history.get(i++));
//        }
//        return historyTopFew;
//    }

    @Override
    public List<PurchaseMembershipHistory> getPurchaseMembershipHistory(User user) {
        List<Purchase> listOfPurchases = user.getListOfPurchases();
        List<PurchaseMembershipHistory> history = new ArrayList<>();
        listOfPurchases
                .stream()
                .filter(p -> p.getPurchasableItem().equals(PurchasableItem.MEMBERSHIP) &&
                        p.getPaymentStatus().equals(PaymentStatus.SUCCESS))
                .forEach(p -> {
                    PurchaseMembershipHistory purchaseHistory = new PurchaseMembershipHistory();
                    purchaseHistory.setDateTime(p.getPurchaseDateTime());
                    purchaseHistory.setPrice(p.getAmount().getTotalAmount());
                    purchaseHistory.setMembershipType(p.getMemberships().get(0).getType().getName());
                    history.add(purchaseHistory);
                });
        Collections.sort(history, Comparator.comparing(PurchaseMembershipHistory::getDateTime).reversed());
        List<PurchaseMembershipHistory> historyTopFew = new ArrayList<>();
        int i = 0;
        while (i < 12 && i < history.size()) {
            historyTopFew.add(history.get(i++));
        }
        return historyTopFew;
    }


    @Override
    public MembershipPageDto getMembershipPageData(User user) {
        MembershipPageDto dto = new MembershipPageDto();
        String profilePic = user.getProfilePicture();
        if (profilePic == null) profilePic = "0";
        dto.setProfilePic(profilePic);
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCompanyName(user.getCompany() == null ? null : user.getCompany().getName());
        dto.setCompanyLogo(user.getCompany() == null || user.getCompany().getLogo() == null
                ? "0" : user.getCompany().getLogo());
        dto.setCurrentMembershipType(user.getMemberships() == null ||
                user.getMemberships().isEmpty() ? null : user.getMemberships().get(user.getMemberships().size() - 1).getType().getName());
        dto.setMembershipDueDate(user.getMemberships() == null ||
                user.getMemberships().isEmpty() ? null : getFullFormattedDate(user.getMemberships().get(user.getMemberships().size() - 1).getDueDate()));
        UserRole userRole = user.getRole();
        if (userRole == null) {
            dto.setRole(null);
        } else if (userRole.equals(UserRole.ADMIN)) {
            dto.setRole("Administrator");
        }
        boolean isEligibleForAdmin = user.getMemberships() != null && !user.getMemberships().isEmpty();
        dto.setEligibleForAdmin(isEligibleForAdmin);
        dto.setPurchaseMembershipHistory(getPurchaseMembershipHistory(user));
        dto.setUpcomingPayment(calculateUpcomingPayment(user));
        Membership membership = user.getMemberships() == null ||
                user.getMemberships().isEmpty() ? null : user.getMemberships().get(user.getMemberships().size() - 1);
        dto.setMembershipStartDate(membership == null ? null : getShortFormattedDate(membership.getMembershipStartDate()));
        dto.setMembershipEndDate(membership == null ? null : getShortFormattedDate(membership.getMembershipEndDate()));
        dto.setMembershipPlan(membership == null ? 0 : membership.getMembershipPlan().getId());
//        dto.setAddOns(membership == null ? null : .getAddOns());
        return dto;
    }

    private String getShortFormattedDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        return date.format(formatter);
    }

    private String getFullFormattedDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
        return date.format(formatter);
    }

    private float calculateUpcomingPayment(User user) {
//        Membership membership = user.getMembership();
//        if (membership == null) return 0;
//        Amount amount = calculateAmount.calculateMembershipAmount(user.getMembership().getType(), user);
//        boolean isLateFee = amount.getLateFee() != 0;
//        List<Amount> amountList = new ArrayList<>();
//        amountList.add(amount);
//        amountList = calculateAmount.calculateAddOnsPrice(membership.getAddOns(), isLateFee, amountList);
        FinalAmount amount = payNow(user);
        return amount == null ? 0 : amount.getTotalAmount();
    }

    @Override
    public void sendAdminConfirmationMail(User user) {
        String content = "Admin request accepted, you are now an admin";
        emailService.sendEmail(user.getEmail(), "Admin Request Status", content);
    }

    @Override
    public void uploadBillingInfo(User user, BillingInfoDto dto) {
        BillingInfo info = new BillingInfo();
        info.setFullName(dto.getFullName());
        info.setBillingEmail(dto.getBillingEmail());
        info.setAddressLine1(dto.getAddressLine1());
        info.setAddressLine2(dto.getAddressLine2());
        info.setContactNumber(dto.getContactNumber());
        billingInfoRepository.save(info);
        user.setBillingInfo(info);
        save(user);
    }

    @Override
    public void uploadContactDetails(User user, ContactDetailsDto dto) {
        ContactDetails details = new ContactDetails();
        user.setPhoneNumber(dto.getContactNumber());
        details.setAddressLine1(dto.getAddressLine1());
        details.setAddressLine2(dto.getAddressLine2());
        contactDetailsRepository.save(details);
        user.setContactDetails(details);
        save(user);
    }

    @Override
    public BillingInfoDto getBillingInfo(User user) {
        if (user.getBillingInfo() != null) return new BillingInfoDto(user.getBillingInfo());
        return null;
    }

    @Override
    public ContactDetailsDto getContactDetails(User user) {
        ContactDetailsDto dto = new ContactDetailsDto();
        dto.setContactNumber(user.getPhoneNumber());
        if (user.getContactDetails() != null) {
            dto.setAddressLine1(user.getContactDetails().getAddressLine1());
            dto.setAddressLine2(user.getContactDetails().getAddressLine2());
        }
        dto.setEmail(user.getEmail());
        return dto;
    }

    @Override
    public boolean billingAddressExist(User user) {
        return user.getBillingInfo() != null;
    }

    @Override
    public boolean checkMembership(User user) {
        return user.getMemberships() != null && !user.getMemberships().isEmpty();
    }

    @Override
    public FinalAmount payNow(User user) {
        log.info("================inside payNow in UserServiceImpl=============");
        List<Membership> memberships = user.getMemberships();
        if (memberships == null || memberships.isEmpty()) {
            log.info("inside if memberships == null || memberships.isEmpty()");
            return null;
        }
        List<Amount> membershipAmountList = membershipService.getCurrentMembershipsPurchaseAmount(memberships);
        List<AddOn> addOns = user.getAddOns();
        List<Amount> addOnsAmountList;
        if (addOns != null && !addOns.isEmpty()) {
            addOnsAmountList = membershipService.getCurrentAddOnsPurchaseAmount(addOns);
            membershipAmountList.addAll(addOnsAmountList);
        }
        float total = calculateAmount.calculateTotalAmountListPrice(membershipAmountList);
        return new FinalAmount(membershipAmountList, total);
    }

    @Override
    public void removeEmployee(User user) {
        log.info("================inside removeEmployee in UserServiceImpl=============");
        user.setIsAddedByAdminToEmployeeList(false);
        userRepository.save(user);
    }
}