package com.shoperal.core.dto;

/**
 * 
 * @author Julius Krah
 *
 */
public enum CategorySortOrder {
	PRICE_ASC, // by price, in ascending order (lowest - highest)
	PRICE_DESC, // by price, in descending order (highest - lowest)
	ALPHA_ASC, // alphabetically, in ascending order (A - Z)
	ALPHA_DESC, // alphabetically, in descending order (Z - A)
	CREATED_ASC, // by date created, in ascending order (oldest - newest)
	CREATED_DESC, // by date created, in descending order (newest - oldest)
	BEST_SELLING, // by best-selling products
}
