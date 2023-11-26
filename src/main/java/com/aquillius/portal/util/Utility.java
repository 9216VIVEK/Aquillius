package com.aquillius.portal.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utility {
	
	/*  Convert date to String in Specific format Like 'May 15, 2023'. */
	public static String dateToStringfmt(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        return  localDate.format(formatter);
	}
	
	/*  Convert date to String in Specific format Like '2023-09-30'. */
	public static String dateToStringfmt1(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return  localDate.format(formatter);
	}
	
	

}
