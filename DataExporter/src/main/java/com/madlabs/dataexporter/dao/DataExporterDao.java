package com.madlabs.dataexporter.dao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component("dataExporterDao")
public class DataExporterDao {
 
	private static final Logger LOGGER = Logger
			.getLogger(DataExporterDao.class);

	@Autowired
	@Qualifier("jdbcTemplate")
	JdbcTemplate jdbcTemplate;

	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
	public SqlRowSet executeQuery(final String query) {
		LOGGER.info("Fetch Size :"+jdbcTemplate.getFetchSize()+".\tExecuting Query");
		SqlRowSet resultSet = jdbcTemplate.queryForRowSet(query);
		return resultSet;
	}
}