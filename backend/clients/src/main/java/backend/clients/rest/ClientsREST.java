package backend.clients.rest;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import backend.clients.dto.ClientBookingsDTO;
import backend.clients.dto.ClientDTO;
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
    public ResponseEntity<Client> createClient(@RequestBody ClientDTO client) {
        Client createdClient = clientService.addClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
    }

    @GetMapping("clientes/{code}")
    public ResponseEntity<Client> getClient(@PathVariable("code") int code) {
        Client client = clientService.getClient(code);
        return ResponseEntity.status(HttpStatus.OK).body(client);
    }

    @GetMapping("clientes/email/{email}")
    public ResponseEntity<Client> getClientByEmail(@PathVariable("email") String email) {
        Client client = clientService.getClientByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(client);
    }

    //Retorna uma lista de códigos de reservas do histórico do cliente
    @GetMapping("clientes/{code}/reservas")
    public ResponseEntity<ClientBookingsDTO> getClientBookings(@PathVariable("code") int code) {
        ClientBookingsDTO clientBookingsDTO = clientService.getClientBookings(code);
        return ResponseEntity.status(HttpStatus.OK).body(clientBookingsDTO);
    }

    @PutMapping("clientes/{code}/milhas")
    public ResponseEntity<MilesBalanceDTO> addClientMiles(@RequestBody Double miles, @PathVariable("code") int code) {
        MilesBalanceDTO milesBalanceDTO = clientService.addMiles(code, miles);
        return ResponseEntity.status(HttpStatus.OK).body(milesBalanceDTO);
    }

    @GetMapping("clientes/{code}/milhas")
    public ResponseEntity<MilesTransactionDTO> getMilesTransactions(@PathVariable("code") int code) {
        MilesTransactionDTO milesTransactionDTO = clientService.getMilesTransactions(code);
        return ResponseEntity.status(HttpStatus.OK).body(milesTransactionDTO);
    }
    
}
