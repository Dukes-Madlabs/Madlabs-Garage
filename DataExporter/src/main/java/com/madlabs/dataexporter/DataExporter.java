package com.madlabs.dataexporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DataExporter {

	private static final Logger LOGGER = Logger.getLogger(DataExporter.class);

	//private static String fileName = "D:/WorkSpace/EXE-TEST-ENV/DX/query.sql";
	private static String fileName = "./query.sql";

	public static void main(String[] args) throws InterruptedException {

		try {
			LOGGER.info("Starting Data Exporter ");
			ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
					"classpath:app-context.xml");
			ExporterService service = context.getBean("exporterService",
					ExporterService.class);
			LOGGER.info("Reading Input Query ");
			final String query = readQuery();
			service.createReport(query);
			LOGGER.info("Shutting Down : Export Completed");
			context.close();
			Thread.sleep(10000000);
		} catch (Exception e) {
			LOGGER.error("Fatel Error", e);
			e.printStackTrace();
			Thread.sleep(10000000);
		}
	}

	public static String readQuery() throws Exception {

		StringBuilder query = new StringBuilder();
		// try {
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				fileName)));
		String split = reader.readLine();
		while (split != null) {
			query.append(split);
			split = reader.readLine();
		}
		reader.close();
		// } catch (IOException e) {
		/*
		 * LOGGER.error(
		 * "Unable to read Query from file. Ensure file is available in " +
		 * "DataExporter's Current Directory", e.getCause());
		 */
		// }
		LOGGER.info("Query :" + query);
		return query.toString();
	}
}