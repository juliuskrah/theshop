package com.shoperal.core.repository;

import static jakarta.persistence.metamodel.Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
import static jakarta.persistence.metamodel.Attribute.PersistentAttributeType.MANY_TO_MANY;
import static jakarta.persistence.metamodel.Attribute.PersistentAttributeType.MANY_TO_ONE;
import static jakarta.persistence.metamodel.Attribute.PersistentAttributeType.ONE_TO_MANY;
import static jakarta.persistence.metamodel.Attribute.PersistentAttributeType.ONE_TO_ONE;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Attribute.PersistentAttributeType;
import jakarta.persistence.metamodel.Bindable;
import jakarta.persistence.metamodel.PluralAttribute;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class QueryUtilsWrapper {
	private static final Map<PersistentAttributeType, Class<? extends Annotation>> ASSOCIATION_TYPES;

	static {
		Map<PersistentAttributeType, Class<? extends Annotation>> persistentAttributeTypes = new HashMap<>();
		persistentAttributeTypes.put(ONE_TO_ONE, OneToOne.class);
		persistentAttributeTypes.put(ONE_TO_MANY, null);
		persistentAttributeTypes.put(MANY_TO_ONE, ManyToOne.class);
		persistentAttributeTypes.put(MANY_TO_MANY, null);
		persistentAttributeTypes.put(ELEMENT_COLLECTION, null);

		ASSOCIATION_TYPES = Collections.unmodifiableMap(persistentAttributeTypes);
	}

	public static <T> Expression<T> toExpressionRecursively(From<?, ?> from, PropertyPath property) {
		return toExpressionRecursively(from, property, false);
	}

	static Expression<Object> toExpressionRecursively(Path<Object> path, PropertyPath property) {

		Path<Object> result = path.get(property.getSegment());
		return property.hasNext() ? toExpressionRecursively(result, property.next()) : result;
	}

	@SuppressWarnings("unchecked")
	static <T> Expression<T> toExpressionRecursively(From<?, ?> from, PropertyPath property, boolean isForSelection) {
		Bindable<?> propertyPathModel;
		Bindable<?> model = from.getModel();
		String segment = property.getSegment();
		propertyPathModel = from.get(segment).getModel();

		if (requiresOuterJoin(propertyPathModel, model instanceof PluralAttribute, !property.hasNext(), isForSelection)
				&& !isAlreadyFetched(from, segment)) {
			Join<?, ?> join = getOrCreateJoin(from, segment);
			return (Expression<T>) (property.hasNext() ? toExpressionRecursively(join, property.next(), isForSelection)
					: join);
		} else {
			Path<Object> path = from.get(segment);
			return (Expression<T>) (property.hasNext() ? toExpressionRecursively(path, property.next()) : path);
		}
	}

	/**
	 * Returns whether the given {@code propertyPathModel} requires the creation of
	 * a join. This is the case if we find a optional association.
	 *
	 * @param propertyPathModel may be {@literal null}.
	 * @param isPluralAttribute is the attribute of Collection type?
	 * @param isLeafProperty    is this the final property navigated by a
	 *                          {@link PropertyPath}?
	 * @param isForSelection    is the property navigated for the selection part of
	 *                          the query?
	 * @return whether an outer join is to be used for integrating this attribute in
	 *         a query.
	 */
	private static boolean requiresOuterJoin(@Nullable Bindable<?> propertyPathModel, boolean isPluralAttribute,
			boolean isLeafProperty, boolean isForSelection) {

		if (propertyPathModel == null && isPluralAttribute) {
			return true;
		}

		if (!(propertyPathModel instanceof Attribute)) {
			return false;
		}

		Attribute<?, ?> attribute = (Attribute<?, ?>) propertyPathModel;

		if (!ASSOCIATION_TYPES.containsKey(attribute.getPersistentAttributeType())) {
			return false;
		}

		// if this path is an optional one to one attribute navigated from the not
		// owning side we also need an explicit
		// outer join to avoid https://hibernate.atlassian.net/browse/HHH-12712 and
		// https://github.com/eclipse-ee4j/jpa-api/issues/170
		boolean isInverseOptionalOneToOne = PersistentAttributeType.ONE_TO_ONE == attribute.getPersistentAttributeType()
				&& StringUtils.hasText(getAnnotationProperty(attribute, "mappedBy", ""));

		// if this path is part of the select list we need to generate an explicit outer
		// join in order to prevent Hibernate
		// to use an inner join instead.
		// see https://hibernate.atlassian.net/browse/HHH-12999.
		if (isLeafProperty && !isForSelection && !attribute.isCollection() && !isInverseOptionalOneToOne) {
			return false;
		}
		return getAnnotationProperty(attribute, "optional", true);
	}

	@SuppressWarnings("unchecked")
	private static <T> T getAnnotationProperty(Attribute<?, ?> attribute, String propertyName, T defaultValue) {
		Class<? extends Annotation> associationAnnotation = ASSOCIATION_TYPES
				.get(attribute.getPersistentAttributeType());
		if (associationAnnotation == null) {
			return defaultValue;
		}
		Member member = attribute.getJavaMember();
		if (!(member instanceof AnnotatedElement)) {
			return defaultValue;
		}
		Annotation annotation = AnnotationUtils.getAnnotation((AnnotatedElement) member, associationAnnotation);
		return annotation == null ? defaultValue : (T) AnnotationUtils.getValue(annotation, propertyName);
	}

	private static boolean isAlreadyFetched(From<?, ?> from, String attribute) {
		for (Fetch<?, ?> fetch : from.getFetches()) {
			boolean sameName = fetch.getAttribute().getName().equals(attribute);
			if (sameName && fetch.getJoinType().equals(JoinType.LEFT)) {
				return true;
			}
		}
		return false;
	}

	private static Join<?, ?> getOrCreateJoin(From<?, ?> from, String attribute) {
		for (Join<?, ?> join : from.getJoins()) {
			boolean sameName = join.getAttribute().getName().equals(attribute);
			if (sameName && join.getJoinType().equals(JoinType.LEFT)) {
				return join;
			}
		}
		return from.join(attribute, JoinType.LEFT);
	}

}
