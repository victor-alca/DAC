package backend.clients.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import backend.clients.models.Client;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


 

@CrossOrigin
@RestController
public class ClientsREST {
    

    //TODO: Inserir cliente não cadastrado
    @PostMapping("clientes")
    public Client createClient(@RequestBody Client client) {

        
        return client;
    }

    //TODO: efetuar login do cliente
    @PostMapping("login")
    public String login(@RequestBody String entity) {
        
        
        return "Login successful";
    }

    //TODO: Efetuar logout do cliente
    @PostMapping("logout")
    public String logout(@RequestBody String entity) {
        
        return "Logout successful";
    }

    //TODO: Retornar dados do cliente
    @GetMapping("clientes/{id}")
    public Client getClient(@PathVariable("id") int id) {

        return new Client();
    }

    //TODO: Listar as reservas do cliente
    @GetMapping("clientes/{id}/reservas")
    public String getClientBookings(@PathVariable("id") int id) {
        
        return "Client reservations";
    }

    //TODO: Adicionar milhas ao cliente
    @PutMapping("clientes/{id}/milhas")
    public String addClientMiles(@RequestBody Double miles, @PathVariable("id") int id) {
        //TODO: process POST request
        
        return "OK";
    }

    //TODO: Buscar o extrato de todas as transações com milhas
    @GetMapping("clientes/{id}/milhas")
    public String getAllMilesTransactions(@PathVariable("id") int id) {
        return new String();
    }
    
}
