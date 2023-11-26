package com.aquillius.portal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aquillius.portal.entity.Invoice;
import com.aquillius.portal.model.FinalAmount;
import com.aquillius.portal.repository.FinalAmountRepository;
import com.aquillius.portal.repository.InvoiceRepository;
import com.aquillius.portal.util.InvoiceGeneratorTemplate;

@RestController
@RequestMapping("/portal/")
public class PdfInvoiceDownlaodController {
	
	@Autowired
	InvoiceRepository invoiceService;
	
	@Autowired
	FinalAmountRepository finalAmountRepository;
	
	@GetMapping("/download/{id}/{finalAmtId}")
	public ResponseEntity<?> downloadFile(@PathVariable Long id,@PathVariable Long finalAmtId){

		Map<String, String> responseData = new HashMap<String, String>();

		Invoice invoice = invoiceService.getReferenceById(id);
		
		FinalAmount referenceById = finalAmountRepository.getReferenceById(finalAmtId);
		
		byte[] generateInvoicePdf = InvoiceGeneratorTemplate.generateInvoicePdf(invoice,referenceById);

		try {
			return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pdf"))
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=InvoiceDemo.pdf").body((new ByteArrayResource(generateInvoicePdf)));

		} catch (Exception e) {
			responseData.put("status", "ERROR");
			responseData.put("statusmsg",e.getMessage());
		}
		return new ResponseEntity<>(responseData, HttpStatus.OK);

	}

}
