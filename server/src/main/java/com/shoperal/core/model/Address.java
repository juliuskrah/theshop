package com.shoperal.core.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.Data;

/**
 * @author Julius Krah
 */
@Data
@Entity
public class Address implements Serializable {
    private static final long serialVersionUID = -5380824725507961286L;
    @Id
    private UUID id;
}
