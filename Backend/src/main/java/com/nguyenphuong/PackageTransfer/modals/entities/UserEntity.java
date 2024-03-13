package com.nguyenphuong.PackageTransfer.modals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "users", indexes = {
    @Index(name = "index_users_email", columnList = "email", unique = true),
    @Index(name = "index_users_username", columnList = "username", unique = true),
})
public class UserEntity extends AbstractEntity {
  @Column(name = "first_name")
  @JsonProperty("first_name")
  private String firstName;

  @Column(name = "last_name")
  @JsonProperty("last_name")
  private String lastName;

  @Column(name = "username", nullable = false)
  @JsonProperty("username")
  private String username;

  @Column(name = "password", nullable = false)
  @JsonProperty("password")
  private String password;

  @Column(name = "email")
  @JsonProperty("email")
  private String email;
}
