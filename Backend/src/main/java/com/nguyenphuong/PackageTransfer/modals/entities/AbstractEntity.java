package com.nguyenphuong.PackageTransfer.modals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public class AbstractEntity implements Serializable {
  @Id
  @ReadOnlyProperty
  @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
  private String id = UUID.randomUUID().toString();

  @Column(name = "created_at")
  @JsonProperty("created_at")
  @ReadOnlyProperty
  private Instant createdAt;

  @Column(name = "updated_at")
  @JsonProperty("updated_at")
  @ReadOnlyProperty
  private Instant updatedAt;

  @Version
  @Column
  @ReadOnlyProperty
  private Integer version;

  @PrePersist
  public void prePersist() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = Instant.now();
  }
}
