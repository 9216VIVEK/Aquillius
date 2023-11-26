package com.aquillius.portal.service.serviceImpl;

import com.aquillius.portal.dto.MembershipPurchaseDto;
import com.aquillius.portal.dto.PurchaseDto;
import com.aquillius.portal.dto.PurchaseTokenDto;
import com.aquillius.portal.entity.*;
import com.aquillius.portal.enums.*;
import com.aquillius.portal.model.Amount;
import com.aquillius.portal.model.FinalAmount;
import com.aquillius.portal.repository.MembershipTypeRepository;
import com.aquillius.portal.repository.NotificationRepository;
import com.aquillius.portal.repository.PurchaseRepository;
import com.aquillius.portal.repository.UserRepository;
import com.aquillius.portal.service.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SquarePaymentServiceImpl implements SquarePaymentService {

    private final MembershipTypeRepository membershipTypeRepository;

    private final MembershipService membershipService;

    private final PurchaseService purchaseService;

    @Autowired
    PurchaseRepository purchaseRepository;

    private final InvoiceService invoiceService;

    private final UserService userService;

    private final EmailService emailService;

    private final NotificationRepository notificationRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public String makeMembershipPayment(PurchaseTokenDto purchaseTokenDto, User user, FinalAmount amount) throws Exception {
        log.info("inside makeMembership Payment");

        MembershipPurchaseDto membershipPurchaseDto = purchaseTokenDto.purchaseDto();

        log.debug("no of memberships = " + membershipPurchaseDto.noOfMemberships());

        LocalDateTime currentDateTime = LocalDateTime.now();
        MembershipType membershipType = membershipTypeRepository.findById(membershipPurchaseDto.membershipTypeId());

        MembershipPlan membershipPlan = null;
        if (membershipPurchaseDto.membershipPlan() != 0) {
            membershipPlan = membershipPurchaseDto.membershipPlan() == MembershipPlan.ANNUAL.getId()
                    ? MembershipPlan.ANNUAL : MembershipPlan.MONTHLY;
        }

        AnnualPaymentType annualPaymentType = null;
        if (membershipPurchaseDto.membershipAnnualPaymentType() != 0) {
            annualPaymentType = membershipPurchaseDto
                    .membershipAnnualPaymentType() == AnnualPaymentType.ANNUAL.getId()
                    ? AnnualPaymentType.ANNUAL : AnnualPaymentType.MONTHLY;
        }

        System.out.println("=========startDate=" + membershipPurchaseDto.startDate() + "===========");


        //handle pay now or normal payment here
        ////////////////////////////////////////////////////////////////////////////

        List<Membership> memberships = new ArrayList<>();
        List<AddOn> addOns;
        if (membershipPurchaseDto.payNow() == 1) {
            log.info("inside if");
            memberships = membershipService.changeCurrentMembership(user);
            addOns = membershipService.changeCurrentAddOns(user);
        } else {
            log.info("inside else");
            Membership membership = membershipService.createMembership(membershipType, membershipPlan,
                    annualPaymentType, user, membershipPurchaseDto.startDate(),
                    membershipPurchaseDto.addOnList(), membershipPurchaseDto.noOfMemberships());
            addOns = membershipService.createAddOns(membershipPurchaseDto.addOnList(), membershipPurchaseDto.startDate(),
                    membershipPlan, annualPaymentType);
            membershipService.save(membership);
            memberships.add(membership);
            if (user.getMemberships() != null) {
                user.getMemberships().add(membership);
            } else {
                List<Membership> list = new ArrayList<>();
                list.add(membership);
                user.setMemberships(list);
            }
            if(user.getAddOns()!=null){
                user.getAddOns().addAll(addOns);
            }
            else{
                user.setAddOns(addOns);
            }
            userService.save(user);
        }


        ///////////////////////////////////////////////////////////////////////////////////////////


//        Purchase purchase = purchaseService.generateMembershipPurchase(currentDateTime,
//                PaymentStatus.SUCCESS, amount);

        Purchase purchase=new Purchase();
        purchase.setPurchasableItem(PurchasableItem.MEMBERSHIP);
        purchase.setPurchaseDateTime(currentDateTime);
        purchase.setPaymentStatus(PaymentStatus.SUCCESS);
        purchase.setAmount(amount);
        purchase.setMemberships(memberships);
        purchase.setAddOns(addOns);

        purchaseRepository.save(purchase);
        user.getListOfPurchases().add(purchase);
        userService.save(user);


        Invoice invoice = invoiceService.generateInvoice(user, amount, PaymentStatus.SUCCESS, purchase,
                currentDateTime, PaymentType.SQUARE_ONLINE);
        invoiceService.save(invoice);

        byte[] invoiceFile = invoiceService.downloadInvoicePdf(invoice.getInvoiceToken());
        emailService.sendEmailWithAttachment(user.getEmail(), "Invoice", "Attached is your invoice.",
                "invoice.pdf", invoiceFile);
        String message = "Invoice sent on mail";
        Notification notification = new Notification(message, LocalDateTime.now());
        notificationRepository.save(notification);
        List<Notification> list = user.getNotifications();
        if (list == null) list = new ArrayList<>();
        list.add(notification);
        user.setNotifications(list);

        userService.save(user);
        return invoice.getInvoiceFilePath();
    }

}
