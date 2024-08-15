package com.shoperal.core.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.Data;

/**
 * @author Julius Krah
 */
@Data
@Entity
public class Menu {
    @Id
    private UUID id;
    private String title;
    private String handle;
}
