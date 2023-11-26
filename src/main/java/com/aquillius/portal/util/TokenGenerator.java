package com.aquillius.portal.util;


import com.aquillius.portal.entity.Invoice;
import lombok.extern.slf4j.Slf4j;

@Slf4j

public class TokenGenerator {
    public static String generateInvoiceToken(Invoice invoice) {
        log.info("============= inside generateInvoiceToken =============");
        StringBuilder token = new StringBuilder(7);
        token.append('#');
        log.debug("token = " + token);
        String idString = String.valueOf(invoice.getId());
        log.debug("idString = " + idString);
        int zerosToAdd = 6 - idString.length();
        log.debug("zeros to add = " + zerosToAdd);
        for (int i = 0; i < zerosToAdd; i++) {
            token.append('0');
        }
        log.debug("token = " + token);
        token.append(idString);
        log.debug("token = " + token);
        return token.toString();
    }
}
