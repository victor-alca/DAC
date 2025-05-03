package com.booking.auth.auth.DTO;

import java.io.Serializable;

public class AuthDTO implements Serializable {
  private String email;
  private String password;

  public AuthDTO() {
    super();
  }

  public AuthDTO(String email, String password) {
    super();
    this.email = email;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
