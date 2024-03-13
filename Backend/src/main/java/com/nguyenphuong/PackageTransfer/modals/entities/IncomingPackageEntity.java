package com.nguyenphuong.PackageTransfer.modals.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nguyenphuong.PackageTransfer.enums.IncomingPackageStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "incoming_packages")
public class IncomingPackageEntity extends AbstractEntity {
  @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
  private String payload;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private IncomingPackageStatus status;

  @Column(name = "method", nullable = false)
  @JsonProperty("method")
  private String method;

  @Column(name = "created_by", nullable = false)
  @JsonProperty("created_by")
  private String createdBy;

  @JsonProperty("target_topics")
  @CollectionTable(name = "incoming_package_target_topics", indexes = {
      @Index(name = "index_incoming_package_target_topics_incoming_package_id", columnList = "incoming_package_entity_id")
  })
  @ElementCollection(fetch = FetchType.EAGER)
  private Set<String> targetTopics = new HashSet<>();

  @ToString.Exclude
  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "incomingPackageEntity")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private List<OutgoingPackageEntity> outgoingPackageEntities;

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
