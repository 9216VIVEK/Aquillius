package com.aquillius.portal.util;

import com.aquillius.portal.entity.Invoice;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import java.io.IOException;

public class PdfInvoiceGenerator {

    public static byte[] generateInvoicePdf(Invoice invoice) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Add invoice details to contentStream, e.g., invoiceDateTime, user details, etc.
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 50);
        contentStream.showText("Invoice Details : invoice token : " + invoice.getInvoiceToken());
        // Add more invoice details
        contentStream.endText();
        contentStream.close();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.save(byteArrayOutputStream);
        document.close();
        return byteArrayOutputStream.toByteArray();
    }
}

