package backend.clients.dto;

public class ClientResponseDTO {
    public int codigo;
    public String cpf;
    public String email;
    public String nome;
    public double saldo_milhas;
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