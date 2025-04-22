package backend.clients.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.clients.models.Client;

public interface ClientRepository extends JpaRepository<Client, String> {
    Client findByCpf(String cpf);
}
