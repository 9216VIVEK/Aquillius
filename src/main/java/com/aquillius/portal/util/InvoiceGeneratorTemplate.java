package com.aquillius.portal.util;

import java.time.LocalDate;

import com.aquillius.portal.model.Amount;
import com.aquillius.portal.model.FinalAmount;
import org.springframework.core.io.ClassPathResource;

import com.aquillius.portal.entity.Invoice;
import com.itextpdf.io.IOException;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

public class InvoiceGeneratorTemplate {

	public static byte[] generateInvoicePdf(Invoice invoice, FinalAmount amount) throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);

		PdfDocument pdfDocument = new PdfDocument(pdfWriter);

		pdfDocument.setDefaultPageSize(PageSize.A4);

		Document document = new Document(pdfDocument);

		float threeCol = 190f;
		float twocol = 285f;
		float twocol150 = twocol + 150f;
		float sixColWidth[] = { threeCol, threeCol, threeCol, threeCol, threeCol, threeCol };

		float sevenColWidth[] = { threeCol, threeCol, threeCol, threeCol, threeCol, threeCol, threeCol };


		float threeColWidth[] = { threeCol, threeCol, threeCol };
		float twocolumnwidth[] = { twocol150, twocol };

		float[] oneColumnWidth = { twocol150 };

		float fullwidth[] = { threeCol * 3 };

		Table table = new Table(sixColWidth);

		Image image = null;

		try {
			ClassPathResource resource = new ClassPathResource("Aquillius.png");
			image = new Image(ImageDataFactory.create(resource.getURL())).setBorder(Border.NO_BORDER);
		} catch (Exception e) {
			e.printStackTrace();
		}

		table.addCell(new Cell().add(image).setFontSize(4f).setBorder(Border.NO_BORDER));

		Table oneColumnWidthTable = new Table(oneColumnWidth).setBorder(Border.NO_BORDER);
		oneColumnWidthTable.addCell(getCell10fLeft("Aquillius Corporation", true));
		oneColumnWidthTable.addCell(getCell10fLeft("""
				10918 Technology Pl
				SAN DIEGO, CA 92127 United States
				info@aquillius.com | 858-533-7500
				""", false).setPaddingTop(-4f));
		table.addCell(new Cell(1, 4).add(oneColumnWidthTable.setTextAlignment(TextAlignment.LEFT))
				.setBorder(Border.NO_BORDER));

//		table.addCell(new Cell(1,2).add("").setBorder(Border.NO_BORDER));
		Table oneColumnWidthTable1 = new Table(oneColumnWidth).setBorder(Border.NO_BORDER);
		oneColumnWidthTable1.addCell(new Cell(1, 2).add("Invoice " + String.format("#" + "%06d", invoice.getId()))
				.setFontSize(8f).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
		oneColumnWidthTable1.addCell(getCell10fLeft("Issue date", true).setTextAlignment(TextAlignment.RIGHT));
		oneColumnWidthTable1
				.addCell(getCell10fLeft(Utility.dateToStringfmt1(invoice.getInvoiceDateTime().toLocalDate()), false)
						.setTextAlignment(TextAlignment.RIGHT).setPaddingTop(-4f));
		table.addCell(new Cell().add(oneColumnWidthTable1).setBorder(Border.NO_BORDER));

		document.add(table.setMarginBottom(15f));

		Color customColor = new DeviceRgb(0xFB, 0x94, 0x71);
		Border gb = new SolidBorder(customColor, 2f);
		Table divider = new Table(fullwidth);
		divider.setBorder(gb);

		document.add(divider.setMarginBottom(15f));
		Paragraph comHeading = new Paragraph("PURCHASE MEMBERSHIP")
				.setFontSize(21f).setTextAlignment(TextAlignment.CENTER);
		document.add(comHeading.setMarginBottom(15f));

		Table lightGray = new Table(threeColWidth);
		Border lightgb = new SolidBorder(Color.LIGHT_GRAY, 0.5f);
		lightGray.setBorder(lightgb);

		document.add(lightGray.setMarginBottom(15f));

		Table threeColumnWidthTableHeading = new Table(threeColWidth);
		threeColumnWidthTableHeading.addCell(getCell10fLeft("Customer", true).setTextAlignment(TextAlignment.LEFT));
		threeColumnWidthTableHeading
				.addCell(getCell10fLeft("Invoice Details", true).setTextAlignment(TextAlignment.LEFT));
		threeColumnWidthTableHeading.addCell(getCell10fLeft("Payment", true).setTextAlignment(TextAlignment.LEFT));
		document.add(threeColumnWidthTableHeading.setMarginBottom(-4f));

		Table threeColumnWidthTableValues = new Table(threeColWidth);
		threeColumnWidthTableValues
				.addCell(getCell10fLeft(invoice.getUser().getFirstName() + " " + invoice.getUser().getLastName(), false)
						.setTextAlignment(TextAlignment.LEFT));

		threeColumnWidthTableValues
				.addCell(getCell10fLeft("PDF created " + Utility.dateToStringfmt(LocalDate.now()), false)
						.setTextAlignment(TextAlignment.LEFT).setPaddingLeft(-1f));
		threeColumnWidthTableValues
				.addCell(getCell10fLeft("$" + amount.getTotalAmount(), false).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(-1f));
		threeColumnWidthTableValues.addCell(getCell10fLeft(invoice.getUser().getEmail(), false)
				.setTextAlignment(TextAlignment.LEFT).setPaddingTop(-4f));
		threeColumnWidthTableValues.addCell(getCell10fLeft("", false)
				.setTextAlignment(TextAlignment.LEFT).setPaddingTop(-4f).setPaddingLeft(-1f));
		document.add(threeColumnWidthTableValues.setMarginBottom(-4f));

		Table oneColumnWidthTableAdd = new Table(oneColumnWidth);
		oneColumnWidthTableAdd.addCell(getCell10fLeft(invoice.getUser().getPhoneNumber(), false));
		if(invoice.getUser().getBillingInfo()!=null) {
			oneColumnWidthTableAdd.addCell(getCell10fLeft(
					invoice.getUser().getBillingInfo().getAddressLine1()+" \n "+invoice.getUser().getBillingInfo().getAddressLine1(),
					false).setPaddingTop(-4f));
		}else {
			oneColumnWidthTableAdd.addCell(getCell10fLeft(
					"",
					false).setPaddingTop(-4f));
		}

		document.add(oneColumnWidthTableAdd.setMarginBottom(10f));

		document.add(lightGray.setMarginBottom(15f));

		Table sixColumnWidthTableAdd = new Table(amount.getInitialPurchase()==1 ?sixColWidth:sevenColWidth);
		sixColumnWidthTableAdd.addCell(getCell10fLeft("Items", true).setTextAlignment(TextAlignment.LEFT));

		sixColumnWidthTableAdd.addCell(getCell10fLeft("", true).setTextAlignment(TextAlignment.LEFT));

		sixColumnWidthTableAdd.addCell(getCell10fLeft("Price", true).setTextAlignment(TextAlignment.RIGHT));

		sixColumnWidthTableAdd.addCell(getCell10fLeft("Quantity", true).setTextAlignment(TextAlignment.RIGHT));
		sixColumnWidthTableAdd.addCell(getCell10fLeft("Total Price", true).setTextAlignment(TextAlignment.RIGHT));
		if(amount.getInitialPurchase()!=1) {
			sixColumnWidthTableAdd.addCell(getCell10fLeft("Late Fee", true).setTextAlignment(TextAlignment.RIGHT));

		}
		sixColumnWidthTableAdd.addCell(getCell10fLeft("Total Amount", true).setTextAlignment(TextAlignment.RIGHT));
		document.add(sixColumnWidthTableAdd.setMarginBottom(8f));

		document.add(lightGray.setMarginBottom(15f));

		for(Amount amt:amount.getAmountList()) {

			if(amount.getInitialPurchase()==1) {

				Table sixColumnWidthTableAdd1 = new Table(sixColWidth);
				sixColumnWidthTableAdd1.addCell(new Cell(1, 2).add(amt.getItem()).setFontSize(10f)
						.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
				sixColumnWidthTableAdd1.addCell(getCell10fLeft(""+amt.getPrice(), false).setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(-20f));
				sixColumnWidthTableAdd1.addCell(getCell10fLeft(""+amt.getQuantity(), false).setTextAlignment(TextAlignment.RIGHT));
				sixColumnWidthTableAdd1
						.addCell(getCell10fLeft("$" + amt.getAmount(), false).setTextAlignment(TextAlignment.RIGHT));
				sixColumnWidthTableAdd1.addCell(getCell10fLeft("$" + amt.getAmount(), false).setTextAlignment(TextAlignment.RIGHT));
				document.add(sixColumnWidthTableAdd1.setMarginBottom(8f));

			}else {

				Table sixColumnWidthTableAdd1 = new Table(sevenColWidth);
				sixColumnWidthTableAdd1.addCell(new Cell(1, 2).add(amt.getItem()).setFontSize(10f).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
				sixColumnWidthTableAdd1.addCell(getCell10fLeft(""+amt.getPrice(), false).setTextAlignment(TextAlignment.RIGHT).setPaddingLeft(-20f));
				sixColumnWidthTableAdd1.addCell(getCell10fLeft(""+amt.getQuantity(), false).setTextAlignment(TextAlignment.RIGHT));
				sixColumnWidthTableAdd1.addCell(getCell10fLeft("$" + amt.getAmount(), false).setTextAlignment(TextAlignment.RIGHT));
				sixColumnWidthTableAdd1.addCell(getCell10fLeft("$" + amt.getLateFee(), false).setTextAlignment(TextAlignment.RIGHT));
				sixColumnWidthTableAdd1.addCell(getCell10fLeft("$" + amt.getAmount(), false).setTextAlignment(TextAlignment.RIGHT));
				document.add(sixColumnWidthTableAdd1.setMarginBottom(8f));

			}
		}


		document.add(lightGray.setMarginBottom(15f));

//		Table twoColumnWidthTableAdd2 = new Table(twocolumnwidth);
//		twoColumnWidthTableAdd2.addCell(getCell10fLeft("Subtotal", false).setTextAlignment(TextAlignment.LEFT));
////twoColumnWidthTableAdd2.addCell(getCell10fLeft("$" + invoice.getAmount(), false).setTextAlignment(TextAlignment.RIGHT));
//		document.add(twoColumnWidthTableAdd2.setMarginBottom(-4));
//
////		Table twoColumnWidthTableAdd3 = new Table(twocolumnwidth);
////		twoColumnWidthTableAdd3.addCell(getCell10fLeft("Sales Tax", false).setTextAlignment(TextAlignment.LEFT));
////		twoColumnWidthTableAdd3.addCell(getCell10fLeft("$0.00", false).setTextAlignment(TextAlignment.RIGHT));
////		document.add(twoColumnWidthTableAdd3.setMarginBottom(-4));
//
//		Table twoColumnWidthTableAdd5 = new Table(twocolumnwidth);
//		twoColumnWidthTableAdd5
//				.addCell(getCell10fLeft("Late Fee", false).setTextAlignment(TextAlignment.LEFT));
////		twoColumnWidthTableAdd5.addCell(getCell10fLeft("$"+invoice.getLateFee(), false).setTextAlignment(TextAlignment.RIGHT));
//		document.add(twoColumnWidthTableAdd5.setMarginBottom(8f));
//
//		document.add(lightGray.setMarginBottom(15f));

		Table twoColumnWidthTableAdd6 = new Table(twocolumnwidth);
		twoColumnWidthTableAdd6
				.addCell(getCell10fLeft("Total Paid", true).setFontSize(15f).setTextAlignment(TextAlignment.LEFT));
		twoColumnWidthTableAdd6
				.addCell(getCell10fLeft("$" + amount.getTotalAmount(), true).setFontSize(15f).setTextAlignment(TextAlignment.RIGHT));
		document.add(twoColumnWidthTableAdd6.setMarginBottom(8f));

		document.add(lightGray.setMarginBottom(15f));

		Paragraph PaymentsHeading = new Paragraph("Payments").setFontSize(7f).setBold();
		document.add(PaymentsHeading.setMarginBottom(-4f));

		Table twoColTable = new Table(twocolumnwidth);
		twoColTable.addCell(getCell10fLeft(Utility.dateToStringfmt(LocalDate.now()), false)).setTextAlignment(TextAlignment.LEFT);
//		twoColTable.addCell(getCell10fLeft("$" + invoice.getAmount(), false).setTextAlignment(TextAlignment.RIGHT));
		document.add(twoColTable.setPaddingLeft(-2f));
		document.close();

		return byteArrayOutputStream.toByteArray();

	}

	public static Cell getCell10fLeft(String value, boolean isBold) {
		Cell cell = new Cell().add(value).setFontSize(8f).setBorder(Border.NO_BORDER);
		return isBold ? cell.setBold() : cell;
	}

}