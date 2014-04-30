package com.madlabs.dataexporter;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;

import com.madlabs.dataexporter.dao.DataExporterDao;
import com.madlabs.dataexporter.util.Util;

@Service("exporterService")
public class ExporterService {  
	
	@Value("${dx.target.fileName}")
	private String targetFileName;
	
	@Value("${dx.target.rowsPerFile}")
	private int max_row_per_file;
	
	@Value("${dx.target.windowsize}")
	private int sheetWindowSize;

	private static final Logger LOGGER = Logger.getLogger(ExporterService.class);

	private List<String> columnName = new LinkedList<String>();

	@Autowired
	DataExporterDao dao;

	public void createReport(final String query) {
		
		LOGGER.info("targetFileName :"+targetFileName);
		LOGGER.info("max row per file :"+max_row_per_file);
		LOGGER.info("Random access window size :"+sheetWindowSize);
		
		SqlRowSet resultSet = dao.executeQuery(query);
		SqlRowSetMetaData metaData = resultSet.getMetaData();
		for (int id = 0; id < metaData.getColumnNames().length; id++) {
			columnName.add(metaData.getColumnNames()[id]);
		}
		LOGGER.info("Starting Report creation");
		SXSSFWorkbook xssfWorkBook = writeResultSetMetaData(columnName);

		writeResultSet(resultSet, xssfWorkBook);
	}

	/**
	 * 
	 * @param result
	 * @param sheet
	 */
	public SXSSFWorkbook writeResultSetMetaData(List<String> resultSet) {
		SXSSFWorkbook xssfWorkBook = new SXSSFWorkbook();
		xssfWorkBook.setCompressTempFiles(true);
		Sheet sheet = xssfWorkBook.createSheet("Data Exporter");
		Row row = sheet.createRow(0);
		for (int id = 0; id < resultSet.size(); id++) {
			Cell cell = row.createCell(id);
			cell.setCellValue(resultSet.get(id));
		}
		return xssfWorkBook;
	}

	public void writeResultSet(SqlRowSet resultSet, SXSSFWorkbook resultBook) {
		
		Integer fileID = 1;
		Sheet sheet = null;
		//String[] columnName = resultSet.getMetaData().getColumnNames();
		resultSet.last();
		int lastRow = resultSet.getRow();
		LOGGER.info("Total Records Retrived :"+lastRow);
		resultSet.beforeFirst();
		int rowID = 1;
		while (resultSet.next()) {
			
			if(resultSet.isFirst()){
				sheet = resultBook.getSheetAt(0);
			}
			Row row = sheet.createRow(rowID);
			for (int colID = 0, id = 2; colID < columnName.size(); colID++, id++) {
				Cell cell = row.createCell(colID);
				cell.setCellValue(resultSet.getString(id - 1));
			}// end of cell iteration
			if(rowID % max_row_per_file == 0){
				rowID = 0;
				LOGGER.info("Writing "+fileID+ " file.");
				Util.workBookWriter(resultBook, targetFileName+fileID+".xlsx");
				resultBook  = createNewWorkBook();
				sheet = resultBook.getSheetAt(0);
				fileID++;
			}
			if(resultSet.getRow() == lastRow){
				LOGGER.info("Writing last set of record into workbook");
				Util.workBookWriter(resultBook, targetFileName+fileID+".xlsx");
			}
			rowID++;
		}// end of row iteration
	}// End of method

	private SXSSFWorkbook createNewWorkBook() {
		SXSSFWorkbook xssfWorkBook = new SXSSFWorkbook(sheetWindowSize);
		xssfWorkBook.setCompressTempFiles(true);
		Sheet sheet = xssfWorkBook.createSheet("Data Exporter");
		Row row = sheet.createRow(0);
		for (int id = 0; id < this.columnName.size(); id++) {
			Cell cell = row.createCell(id);
			cell.setCellValue(this.columnName.get(id));
		}
		return xssfWorkBook;
	}

}