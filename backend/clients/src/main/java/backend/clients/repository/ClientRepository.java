package backend.clients.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.clients.models.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findByCpf(String cpf);
    Client findByEmail(String email);
}
