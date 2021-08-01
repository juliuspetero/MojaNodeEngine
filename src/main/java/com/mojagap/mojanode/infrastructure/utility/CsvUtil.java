package com.mojagap.mojanode.infrastructure.utility;

import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.security.JwtAuthorizationFilter;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CsvUtil {

    private final static Logger LOG = Logger.getLogger(CsvUtil.class.getName());

    @SneakyThrows(IOException.class)
    public static <T> List<T> parseCsv(String location, Class<T> clazz) {
        LOG.log(Level.INFO, "LOADING CSV FILE :: " + clazz.getSimpleName());
        InputStream inputStream = new FileInputStream(location);
        String csv = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
        return csvStringPojoList(csv, clazz);
    }

    @SneakyThrows(IOException.class)
    public static List<JwtAuthorizationFilter.RequestSecurity> parseSecurityCsv() {
        List<JwtAuthorizationFilter.RequestSecurity> requestSecurities = AppContext.getRequestSecurities();
        if (requestSecurities != null) return requestSecurities;
        LOG.log(Level.INFO, "LOADING CSV FILE :: REQUEST SECURITY PERMISSIONS");
        ClassLoader classLoader = CsvUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("security/security.csv");
        String csv = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
        requestSecurities = csvStringPojoList(csv, JwtAuthorizationFilter.RequestSecurity.class);
        AppContext.setRequestSecurities(requestSecurities);
        return requestSecurities;
    }

    @SneakyThrows(IOException.class)
    public static <T> List<T> parseMultipartCsv(MultipartFile multipartFile, Class<T> clazz) {
        PowerValidator.isTrue(ApplicationConstants.CSV_CONTENT_TYPE.equals(multipartFile.getContentType()), ErrorMessages.CSV_FILE_TYPE_REQUIRED);
        LOG.log(Level.INFO, "LOADING CSV FILE :: " + clazz.getSimpleName());
        InputStream inputStream = multipartFile.getInputStream();
        String csv = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
        return csvStringPojoList(csv, clazz);
    }

    public static <T> List<T> csvStringPojoList(String csv, Class<T> clazz) {
        StringReader stringReader = new StringReader(csv);
        CSVReader csvReader = new CSVReaderBuilder(stringReader).build();
        return new CsvToBeanBuilder<T>(csvReader)
                .withType(clazz)
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .build()
                .parse();
    }
}
