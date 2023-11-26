package com.aquillius.portal.service;

import com.aquillius.portal.entity.Invoice;
import com.aquillius.portal.entity.Purchase;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.enums.PaymentStatus;
import com.aquillius.portal.enums.PaymentType;
import com.aquillius.portal.model.Amount;
import com.aquillius.portal.model.FinalAmount;

import java.io.IOException;
import java.time.LocalDateTime;


public interface InvoiceService {

    Invoice generateInvoice(User user, FinalAmount amount, PaymentStatus paymentStatus,
                            Purchase purchase, LocalDateTime currentDateTime, PaymentType paymentType) throws Exception;

    void confirmInvoice(Invoice invoice) throws Exception;

    void save(Invoice invoice) throws Exception;

    Invoice findByInvoiceToken(String invoiceToken) throws Exception;

    byte[] downloadInvoicePdf(String invoiceToken) throws Exception;
}
