package com.shoperal.core.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductCategory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductCategory that = (ProductCategory) o;
        return Objects.equals(category, that.category) && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, product);
    }
}
