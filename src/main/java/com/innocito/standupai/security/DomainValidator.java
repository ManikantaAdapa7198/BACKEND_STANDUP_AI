package com.innocito.standupai.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DomainValidator {

    private final List<String> allowedDomains;

    public DomainValidator(@Value("${allowed.domains}") String allowedDomainsStr) {
        // Parse the comma-separated string into a list, trim spaces
        allowedDomains = Arrays.stream(allowedDomainsStr.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public boolean isDomainAllowed(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        String domain = email.substring(email.indexOf("@") + 1);
        return allowedDomains.contains(domain.toLowerCase());
    }
}
