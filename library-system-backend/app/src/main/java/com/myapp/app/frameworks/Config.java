package com.myapp.app.frameworks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {

    @Value("${stale.book.months}")
    private Integer staleBookMonths;

    @Value("${book.requests.auto_complete.on.add_to_stock}")
    private Boolean autoCompleteRequests;

    @Value("${data.store.path}")
    private String dataStorePath;

    @Value("${csv.export.models}")
    private String exportCsvModelsPath;

    @Value("${csv.export.orders}")
    private String exportCsvOrdersPath;

    @Value("${csv.import.models}")
    private String importCsvModelsPath;

    @Value("${csv.import.orders}")
    private String importCsvOrdersPath;

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.user}")
    private String dbUser;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${hibernate.connection.driver_class}")
    private String hibernateDriver;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    @Value("${hibernate.hbm2ddl.auto}")
    private String hibernateHbm2ddlAuto;

    public Integer getStaleBookMonths() {
        return staleBookMonths;
    }
    public Boolean isAutoCompleteRequests() {
        return autoCompleteRequests;
    }
    public String getDataStorePath() {
        return dataStorePath; }
    public String getExportCsvModelsPath() {
        return exportCsvModelsPath;
    }
    public String getExportCsvOrdersPath() {
        return exportCsvOrdersPath;
    }
    public String getImportCsvModelsPath() {
        return importCsvModelsPath;
    }
    public String getImportCsvOrdersPath() {
        return importCsvOrdersPath;
    }
    public String getDbUrl() {
        return dbUrl;
    }
    public String getDbUser() {
        return dbUser;
    }
    public String getDbPassword() {
        return dbPassword;
    }
    public String getHibernateDriver() {
        return hibernateDriver;
    }
    public String getHibernateDialect() {
        return hibernateDialect;
    }
    public String getHibernateHbm2ddlAuto() {
        return hibernateHbm2ddlAuto;
    }
}
