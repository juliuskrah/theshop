package com.shoperal.core.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;

import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditEntity implements Auditable<String, UUID, OffsetDateTime>, Serializable {
	private static final long serialVersionUID = 5304890459523857016L;
	@Id
	@GeneratedValue
	private UUID id;
	private OffsetDateTime createdDate;
	private OffsetDateTime lastModifiedDate;
	private String createdBy;
	private String lastModifiedBy;
	@Transient
	private boolean isNew = true;

	@Override
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	@Override
	public Optional<String> getCreatedBy() {
		return Optional.of(this.createdBy);
	}

	@Override
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public Optional<String> getLastModifiedBy() {
		return Optional.of(this.lastModifiedBy);
	}

	@Override
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	@Override
	public Optional<OffsetDateTime> getCreatedDate() {
		return Optional.of(this.createdDate);
	}

	@Override
	public void setCreatedDate(OffsetDateTime createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public Optional<OffsetDateTime> getLastModifiedDate() {
		return Optional.of(this.lastModifiedDate);
	}

	@Override
	public void setLastModifiedDate(OffsetDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	@PrePersist
	@PostLoad
	void markNotNew() {
		this.isNew = false;
	}
}
