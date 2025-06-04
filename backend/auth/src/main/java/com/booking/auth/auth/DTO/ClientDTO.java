package com.booking.auth.auth.DTO;

public class ClientDTO {
  public String cpf;
  public String name;
  public int miles = 0;
  public String email;
  public String phone;
  public String password;
  public String cep;
  public String state;
  public String city;
  public String neighborhood;
  public String street;
  public String number;
  public String complement;

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
