package com.aquillius.portal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/portal/ach")
@RequiredArgsConstructor
public class AchPaymentController {
//
//    private final EmailService emailService;
//
//    private final InvoiceService invoiceService;
//
//    private final UserService userService;
//
//    private final PurchaseService purchaseService;
//
//    private final MembershipService membershipService;
//
//    private final MembershipTypeRepository membershipTypeRepository;
//
//    private final CalculateAmount  calculateAmount;
//
//
//    @PostMapping("/payment")
//    public ResponseEntity<Message> processAchPayment(@RequestBody PurchaseTokenDto dto, Principal principal) {
//
//    	log.info("In the process Ach Payment method");
//
//        User user = userService.findByEmail(principal.getName());
//        LocalDateTime currentDateTime = LocalDateTime.now();
//        Amount amount = dto.purchaseDto().amount();
//
//        PurchaseDto purchaseDto = dto.purchaseDto();
//        MembershipType membershipType =
//                membershipTypeRepository.findById(purchaseDto.membershipPurchaseDto().membershipTypeId());
//
//        MembershipPlan membershipPlan =
//                purchaseDto.membershipPurchaseDto().membershipPlan() == MembershipPlan.ANNUAL.getId() ?
//                        MembershipPlan.ANNUAL : MembershipPlan.MONTHLY;
//
//        AnnualPaymentType annualPaymentType =
//                purchaseDto.membershipPurchaseDto().membershipAnnualPaymentType() == AnnualPaymentType.ANNUAL.getId() ?
//                        AnnualPaymentType.ANNUAL : AnnualPaymentType.MONTHLY;
//
//        Membership membership =
//                membershipService.createMembership(membershipType, membershipPlan, annualPaymentType,
//                        user, purchaseDto.membershipPurchaseDto().startDate());
//
//        membershipService.save(membership);
//
//
//        Purchase purchase = purchaseService.generatePurchase(purchaseDto, currentDateTime,
//                PaymentStatus.PENDING, amount);
//
//        purchase.setMembership(membership);
//        purchaseService.save(purchase);
//
//        user.getListOfPurchases().add(purchase);
//        userService.save(user);
//
//        Invoice invoice =
//                invoiceService.generateInvoice(user, amount, PaymentStatus.PENDING,
//                        purchase, currentDateTime, PaymentType.ACH);
//        invoiceService.save(invoice);
//        try {
//            byte[] invoiceFile = invoiceService.downloadInvoicePdf(invoice.getInvoiceToken());
//            emailService.sendEmailWithAttachment(user.getEmail(), "Invoice", "Attached is your invoice.",
//                    "invoice.pdf", invoiceFile);
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//        return ResponseEntity.ok(new Message(1, "Invoice email sent"));
//    }
//
//    @PostMapping("/confirm")
//    public ResponseEntity<Message> confirmAchPayment(@RequestBody ConfirmAchPayment confirmAchPayment) {
//
//        User user = userService.findByEmail(confirmAchPayment.emailOfUser());
//        Invoice invoice = invoiceService.findByInvoiceToken(confirmAchPayment.invoiceToken());
//        if (invoice == null) {
//            return ResponseEntity.ok(new Message(0, "Invoice not found"));
//        }
//        if (invoice.getUser() != user) {
//            return ResponseEntity.ok(new Message(0, "User and Invoice do not match"));
//        }
//        invoiceService.confirmInvoice(invoice);
//        Purchase purchase = invoice.getPurchase();
//        purchase.setPaymentStatus(PaymentStatus.SUCCESS);
//        purchase.setPurchaseDateTime(LocalDateTime.now());
//        Membership membership=purchase.getMembership();
//        user.setMembership(membership);
//        purchaseService.save(purchase);
//        userService.save(user);
//        try {
//            byte[] invoiceFile = invoiceService.downloadInvoicePdf(invoice.getInvoiceToken());
//            emailService.sendEmailWithAttachment(user.getEmail(), "Invoice", "Attached is your invoice.",
//                    "invoice.pdf", invoiceFile);
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//        return ResponseEntity.ok(new Message(1, "Purchase confirmed"));
//    }
}
