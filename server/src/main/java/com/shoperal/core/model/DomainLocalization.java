package com.shoperal.core.model;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

/**
 * @author Julius Krah
 */
@Data
public class DomainLocalization implements Serializable {
    private static final long serialVersionUID = 5904511091846473314L;
    /**
     * The ISO codes for the domain’s alternate locales
     */
    private Set<String> alternateLocales;
    /**
     * The ISO code for the country assigned to the domain, or "*" for a domain set
     * to "Rest of world"
     */
    private String country;
    /**
     * The ISO code for the domain’s default locale
     */
    private String defaultLocale;
}
