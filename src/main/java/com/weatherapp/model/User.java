package com.weatherapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @NotEmpty
  @Column(name = "username", length = 50, unique = true)
  private String username;

  @NotNull
  @NotEmpty
  @Column(name = "password", length = 256)
  private String password;

  private String matchingPassword;

  @Column(name = "enabled")
  private boolean enabled;

  @NotNull
  @NotEmpty
  @Email
  @Column(name = "email", length = 100, unique = true)
  private String email;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Role> roles;
  public User() {
    super();
    this.enabled = false;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final User user = (User) obj;
    if (!getEmail().equals(user.getEmail())) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((getEmail() == null) ? 0 : getEmail().hashCode());
    return result;
  }
}
