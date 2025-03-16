package org.ricky;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.time.ZoneId.of;
import static java.util.TimeZone.getTimeZone;
import static java.util.TimeZone.setDefault;
import static org.ricky.common.constants.ConfigConstant.CHINA_TIME_ZONE;

@SpringBootApplication
public class AllDocsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllDocsApplication.class, args);
    }

    @PostConstruct
    void init() {
        setDefault(getTimeZone(of(CHINA_TIME_ZONE)));
    }

}
