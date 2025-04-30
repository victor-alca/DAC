package com.booking.auth.auth.model;

import java.io.Serializable;

public class Auth implements Serializable {
  private String email;
  private String password;

  public Auth() {
    super();
  }

  public Auth(String email, String password) {
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
