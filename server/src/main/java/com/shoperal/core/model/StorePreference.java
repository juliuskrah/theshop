package com.shoperal.core.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

import lombok.Data;

/**
 * @author Julius Krah
 */
@Data
@Entity
public class StorePreference implements Serializable {
    private static final long serialVersionUID = 32451768098544L;

    @Id
    private UUID id;
    private String homePageTitle;
    @MapsId
    @JoinColumn(name = "id")
    @OneToOne
    private StoreSetting storeSetting;
}
