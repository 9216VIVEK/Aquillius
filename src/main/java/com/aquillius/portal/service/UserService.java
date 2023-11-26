package com.aquillius.portal.service;

import com.aquillius.portal.dto.*;

import com.aquillius.portal.entity.Membership;
import com.aquillius.portal.entity.User;

import com.aquillius.portal.model.FinalAmount;
import com.aquillius.portal.model.MembershipAmountResponse;
import com.aquillius.portal.model.PurchaseMembershipHistory;
import org.springframework.web.multipart.MultipartFile;


import java.security.Principal;
import java.util.List;

public interface UserService {

    User findByEmail(String email);
    boolean existsByEmail(String email) ;

    void save(User user);

    void uploadProfilePicture(User user, String file) throws Exception;

    String getProfilePicture(User user);


    User createUser(SignUpRequest signupRequest)throws Exception ;

    boolean updateUserDetails(UserUpdateRequest request, User user);

    void resetPassword(User user, String newPassword);

    boolean verifyCurrentPassword(User user, String currentPassword);

    void addEmployee(AddEmployeeDto dto, User user);

    void forgetPassword(String email) throws Exception;

    void applyForAdmin(User user);

    List<PurchaseMembershipHistory> getPurchaseMembershipHistory(User user);

    MembershipPageDto getMembershipPageData(User user);

    void sendAdminConfirmationMail(User user);

    void uploadBillingInfo(User user, BillingInfoDto dto);

    void uploadContactDetails(User user, ContactDetailsDto dto);

    BillingInfoDto getBillingInfo(User user);

    ContactDetailsDto getContactDetails(User user);

    boolean billingAddressExist(User user);

    boolean checkMembership(User user);

    FinalAmount payNow(User user);

    void removeEmployee(User user);
}