package com.aquillius.portal.repository;

import com.aquillius.portal.entity.Invoice;
import com.aquillius.portal.entity.User;
import com.aquillius.portal.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsByInvoiceToken(String invoiceToken);

    Invoice findByInvoiceToken(String invoiceToken);

}
