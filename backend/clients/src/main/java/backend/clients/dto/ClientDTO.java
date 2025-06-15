package backend.clients.dto;

public class ClientDTO {
    public String cpf;
    public String nome;
    public double saldo_milhas;
    public String email;
    public EnderecoDTO endereco;

    public static class EnderecoDTO {
        public String cep;
        public String uf;
        public String cidade;
        public String bairro;
        public String rua;
        public String numero;
        public String complemento;
    }
}

