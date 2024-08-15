package com.shoperal.core.model;

import java.net.URI;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import lombok.Data;

/**
 * @author Julius Krah
 */
@Data
@Entity
public class MenuItem {
    @Id
    private UUID id;
    private String title;
    private URI uri;
    /**
     * Position of the menu. This influences the ordering
     */
    private int position;
    @ManyToOne(fetch = FetchType.LAZY)
    private Menu menu;
}
