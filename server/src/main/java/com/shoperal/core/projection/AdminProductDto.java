package com.shoperal.core.projection;

import java.net.URI;
import java.util.UUID;

import com.shoperal.core.model.ProductStatus;

import lombok.Value;

@Value
public class AdminProductDto {
    UUID id;
    String name;
    String type;
    String vendor;
    Double costPrice;
    Double price;
    Double comparablePrice;
    ProductStatus status;
    String description;
    URI featuredMedia;
    URI friendlyUriFragment;
    Integer inventory;
    // This is a List
    Object tags;
}
