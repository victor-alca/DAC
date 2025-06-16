package com.booking.auth.auth.DTO;

public class EmployeeDTO {
    public String cpf;
    public String email;
    public String nome;
    public String telefone;
    public String senha;
    public String active;
    public int id;

    // Constructors
    public EmployeeDTO() {
    }

    public EmployeeDTO(String cpf, String email, String nome, String telefone, String senha) {
        this.cpf = cpf;
        this.email = email;
        this.nome = nome;
        this.telefone = telefone;
        this.senha = senha;
    }

    // Getters e Setters
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", nome='" + nome + '\'' +
                ", telefone='" + telefone + '\'' +
                ", senha='" + (senha != null ? "***" : "null") + '\'' +
                ", active='" + active + '\'' +
                ", id=" + id +
                '}';
    }
}