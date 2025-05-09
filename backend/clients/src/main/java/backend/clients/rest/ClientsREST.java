package backend.clients.rest;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import backend.clients.dto.MilesBalanceDTO;
import backend.clients.dto.MilesTransactionDTO;
import backend.clients.models.Client;
import backend.clients.repository.ClientRepository;
import backend.clients.services.ClientService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


 

@CrossOrigin
@RestController
public class ClientsREST {
    
    @Autowired
    private ClientService clientService;
    @PostMapping("clientes")
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        clientService.addClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }


    //TODO: Retornar dados do cliente
    @GetMapping("clientes/{id}")
    public ResponseEntity<Client> getClient(@PathVariable("id") String id) {
        Client client = clientService.getClient(id);
        return ResponseEntity.status(HttpStatus.OK).body(client);
    }

    //TODO: Listar as reservas do cliente
    @GetMapping("clientes/{id}/reservas")
    public String getClientBookings(@PathVariable("id") int id) {
        //Request para service de reservas?
        return "Client reservations";
    }

    //TODO: Adicionar milhas ao cliente
    @PutMapping("clientes/{id}/milhas")
    public ResponseEntity<MilesBalanceDTO> addClientMiles(@RequestBody Double miles, @PathVariable("id") String id) {
        MilesBalanceDTO milesBalanceDTO = clientService.addMiles(id, miles);
        return ResponseEntity.status(HttpStatus.OK).body(milesBalanceDTO);
    }

    //TODO: Buscar o extrato de todas as transações com milhas
    @GetMapping("clientes/{id}/milhas")
    public ResponseEntity<MilesTransactionDTO> getMilesTransactions(@PathVariable("id") String cpf) {
        MilesTransactionDTO milesTransactionDTO = clientService.getMilesTransactions(cpf);
        return ResponseEntity.status(HttpStatus.OK).body(milesTransactionDTO);
    }
    
}
