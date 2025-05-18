package com.old.silence.core.jdbc;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class PlatformJdbcTemplate extends JdbcTemplate {

    private static final Logger warningLogger = LoggerFactory.getLogger(PlatformJdbcTemplate.class.getName() + ".WARNING");

    public PlatformJdbcTemplate() {
    }

    public PlatformJdbcTemplate(DataSource dataSource, boolean lazyInit) {
        super(dataSource, lazyInit);
    }

    public PlatformJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void handleWarnings(Statement stmt) throws SQLException {
        if (isIgnoreWarnings()) {
            if (warningLogger.isDebugEnabled()) {
                SQLWarning warningToLog = stmt.getWarnings();
                while (warningToLog != null) {
                    warningLogger.debug("SQLWarning ignored: SQL state '" + warningToLog.getSQLState() + "', error code '"
                            + warningToLog.getErrorCode() + "', message [" + warningToLog.getMessage() + "]");
                    warningToLog = warningToLog.getNextWarning();
                }
            }
        } else {
            handleWarnings(stmt.getWarnings());
        }
    }


}
