package com.madlabs.dataexporter.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class Util {
	
	private static final Logger LOGGER = Logger
			.getLogger(Util.class);
	
	
	public static final int MAX_ROW_PER_FILE = 50;

	public static String getTime() {
		DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z",
				Locale.US);
		return df.format(new Date());
	}

	public static void workBookWriter(SXSSFWorkbook xssfWorkBook, String fileName) {
									  
		FileOutputStream fops;
		try {
			fops = new FileOutputStream(fileName);
			xssfWorkBook.write(fops);
			fops.close();
			LOGGER.info("is POI deleting Temp Files :"+xssfWorkBook.dispose());
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOGGER.info("Work Book created :"+fileName);
	}
}