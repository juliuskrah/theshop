package com.shoperal.core.model;

import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

import lombok.Data;

/**
 * @author Julius Krah
 */
@Data
public class Domain implements Serializable {
    private static final long serialVersionUID = 7067137538338461259L;
    private UUID id;
    /**
     * The host name of the domain (eg: {@code example.com})
     */
    private String host;
    private boolean sslEnabled;
    /**
     * The URL of the domain (eg: {@code https://example.com})
     */
    private URI url;
    /**
     * The localization of the domain, if it does not redirect
     */
    private DomainLocalization localization;
}
