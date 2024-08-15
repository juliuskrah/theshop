package com.shoperal.core.event;

import org.springframework.context.ApplicationEvent;

import com.shoperal.core.model.Category;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class CategoryEvent extends ApplicationEvent {
	private static final long serialVersionUID = -2643127159825977472L;
	private Category category;
	private EventType type;

	public CategoryEvent(Object source, Category category) {
		this(source, category, EventType.ADD);
	}

	public CategoryEvent(Object source, Category category, EventType type) {
		super(source);
		this.category = category;
		this.type = type;
	}
}
