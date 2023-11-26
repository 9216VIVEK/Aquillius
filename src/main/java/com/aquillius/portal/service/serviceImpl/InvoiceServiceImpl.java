package com.aquillius.portal.service.serviceImpl;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.aquillius.portal.model.FinalAmount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aquillius.portal.entity.Invoice;
import com.aquillius.portal.entity.Purchase;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.enums.PaymentStatus;
import com.aquillius.portal.enums.PaymentType;
import com.aquillius.portal.model.Amount;
import com.aquillius.portal.repository.InvoiceRepository;
import com.aquillius.portal.service.InvoiceService;
import com.aquillius.portal.util.InvoiceGeneratorTemplate;
import com.aquillius.portal.util.PdfInvoiceGenerator;
import com.aquillius.portal.util.TokenGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Value("${pdf.upload-dir}")
    private String folderPath; // Same folder path as used in StorageServiceImpl

    public Invoice generateInvoice(User user, FinalAmount amount, PaymentStatus paymentStatus,
                                   Purchase purchase, LocalDateTime currentDateTime, PaymentType paymentType) throws Exception {

        log.info("===================inside generateInvoice Service API ===================");

        Invoice invoice = new Invoice();

        invoice.setUser(user);
        invoice.setInvoiceDateTime(currentDateTime);
        invoice.setInvoiceStatus(paymentStatus);
        invoice.setTotalAmount(amount.getTotalAmount());
        invoice.setPaymentType(paymentType);
        invoice.setPurchase(purchase);
        invoiceRepository.save(invoice);
        String token = TokenGenerator.generateInvoiceToken(invoice);
        invoice.setInvoiceToken(token);
        byte[] invoicePdfData = InvoiceGeneratorTemplate.generateInvoicePdf(invoice, amount);
        String pdfFilePath = folderPath + invoice.getInvoiceToken() + ".pdf";
        Files.write(Paths.get(pdfFilePath), invoicePdfData);
        invoice.setInvoiceFilePath(pdfFilePath);
        invoiceRepository.save(invoice);
        return invoice;
    }

    private String formatTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return localDateTime.format(formatter);
    }

    public void confirmInvoice(Invoice invoice) throws Exception {

        log.info("===================inside confirmInvoice Service API ===================");

        invoice.setInvoiceStatus(PaymentStatus.SUCCESS);
        invoiceRepository.save(invoice);

        byte[] invoicePdfData = PdfInvoiceGenerator.generateInvoicePdf(invoice);
        String pdfFilePath = folderPath + invoice.getInvoiceToken() + formatTime() + ".pdf";
        Files.write(Paths.get(pdfFilePath), invoicePdfData);
        invoice.setInvoiceFilePath(pdfFilePath);
        invoiceRepository.save(invoice);
    }

    public void save(Invoice invoice) {
        invoiceRepository.save(invoice);
    }

    public Invoice findByInvoiceToken(String invoiceToken) {
        return invoiceRepository.findByInvoiceToken(invoiceToken);
    }

    public byte[] downloadInvoicePdf(String invoiceToken) throws Exception {

        log.info("===================inside downloadInvoicePdf Service API ===================");


        Invoice invoice = findByInvoiceToken(invoiceToken);
        if (invoice == null) return null;

        // Read the PDF file as bytes
        Path path = Paths.get(invoice.getInvoiceFilePath());
        if (Files.exists(path)) {
            return Files.readAllBytes(path);
        } else {
            throw new FileNotFoundException("Invoice PDF not found for token: " + invoiceToken);
        }
    }
}
