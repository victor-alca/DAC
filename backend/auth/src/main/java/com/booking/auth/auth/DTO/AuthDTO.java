package com.booking.auth.auth.DTO;

import java.io.Serializable;

public class AuthDTO implements Serializable {
  private String login;
  private String senha;

  public AuthDTO() {
    super();
  }

  public AuthDTO(String login, String senha) {
    super();
    this.login = login;
    this.senha = senha;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getSenha() {
    return senha;
  }

  public void setSenha(String senha) {
    this.senha = senha;
  }
}