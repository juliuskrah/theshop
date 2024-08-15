package com.shoperal.core.model;

import java.net.URI;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Julius Krah
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class StoreSetting extends AbstractAuditEntity {

    private static final long serialVersionUID = -8759703994606594202L;
    /**
     * The store's name
     */
    private String name;
    /**
     * The public facing contact email for the store
     */
    private String contactEmail;
    /**
     * The merchant's email address
     */
    private String email;
    private URI logo;
    @Lob
    private String description;
    @Enumerated(EnumType.STRING)
    private CurrencyCode currencyCode;
    @Enumerated(EnumType.STRING)
    @ElementCollection
    @CollectionTable( //
            name = "presentment_currency", //
            joinColumns = @JoinColumn(name = "store_id") //
    )
    @Column(name = "currency")
    private Set<CurrencyCode> presentmentCurrencies;
    /**
     * IANA assigned time zone
     * {@link https://techsupport.osisoft.com/Documentation/PI-Web-API/help/topics/timezones/iana.html}
     */
    private String ianaTimezone;
    private String storeVersion;
    /**
     * The store's .shoperal.app domain name.
     */
    private String shoperalDomain;
    private boolean setupRequired;
    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;
}
