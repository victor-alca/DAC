package com.booking.auth.auth.DTO;

import com.booking.auth.auth.model.User;

public class LoginResponseDTO {
    private String access_token;
    private String token_type = "bearer";
    private String tipo;
    private User usuario;

    public LoginResponseDTO(String access_token, String tipo, User usuario) {
        this.access_token = access_token;
        this.tipo = tipo;
        this.usuario = usuario;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getTipo() {
        return tipo;
    }

    public User getUsuario() {
        return usuario;
    }
}
