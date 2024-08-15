package com.shoperal.core.dto;

import java.io.Serializable;

public final class Pair<A, B> implements Serializable {

	private static final long serialVersionUID = -2370293253974408398L;
	private final A val0;
	private final B val1;

	public Pair(final A value0, final B value1) {
		this.val0 = value0;
		this.val1 = value1;
	}

	public static <A, B> Pair<A, B> with(final A value0, final B value1) {
		return new Pair<A, B>(value0, value1);
	}

	/**
	 * <p>
	 * Create tuple from array. Array has to have exactly two elements.
	 * </p>
	 * 
	 * @param <X>   the array component type
	 * @param array the array to be converted to a tuple
	 * @return the tuple
	 */
	public static <X> Pair<X, X> fromArray(final X[] array) {
		if (array == null) {
			throw new IllegalArgumentException("Array cannot be null");
		}
		if (array.length != 2) {
			throw new IllegalArgumentException(
					"Array must have exactly 2 elements in order to create a Pair. Size is " + array.length);
		}
		return new Pair<X, X>(array[0], array[1]);
	}

	public A getValue0() {
		return this.val0;
	}

	public B getValue1() {
		return this.val1;
	}

}
