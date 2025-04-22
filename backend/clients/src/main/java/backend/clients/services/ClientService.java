package backend.clients.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.clients.models.Client;
import backend.clients.repository.ClientRepository;



@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;


    public Client addClient(Client newClient) {

        if(clientRepository.findByCpf(newClient.getCpf()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Client already exists");
        }
        clientRepository.save(newClient);
        return newClient;
    }

    public Client getClient (String cpf) {
        Client client = clientRepository.findByCpf(cpf);
        if(client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        return client;
    }

    public Client updateClient (Client client){
        Client findClient = clientRepository.findByCpf(client.getCpf());
        if(findClient == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        clientRepository.save(client);
        return client;
    }
    
    public double addMiles(String cpf, Double miles) {
        Client client = clientRepository.findByCpf(cpf);
        if(client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        client.setMiles(client.getMiles() + miles);
        clientRepository.save(client);
        return client.getMiles();
    }
}