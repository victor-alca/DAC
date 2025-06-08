package backend.clients.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.clients.dto.ClientBookingsDTO;
import backend.clients.dto.MilesBalanceDTO;
import backend.clients.dto.MilesTransactionDTO;
import backend.clients.dto.MilesTransactionDTO.Transaction;
import backend.clients.models.Client;
import backend.clients.models.MilesRecord;
import backend.clients.repository.ClientRepository;
import backend.clients.repository.MilesRecordRepository;



@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository; 
    @Autowired
    private MilesRecordRepository milesRecordRepository;

    public Client addClient(Client newClient) {

        if(clientRepository.findByCpf(newClient.getCpf()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O Cliente já existe!");
        }
        clientRepository.save(newClient);
        return newClient;
    }

    public Client getClient (int code) {
        Client client = clientRepository.findById(code).orElse(null);
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
    
    public MilesBalanceDTO addMiles(int code, Double miles) {
        Client client = clientRepository.findById(code).orElse(null);
        if(client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        client.setMiles(client.getMiles() + miles);
        clientRepository.save(client);
        MilesBalanceDTO milesBalanceDTO = new MilesBalanceDTO(1, client.getMiles());
        return milesBalanceDTO;
    }

    public boolean debitarMilhas(int codigoCliente, double milhasParaDebitar) {
        Client client = clientRepository.findById(codigoCliente).orElse(null);
        if (client == null) {
            return false;
        }
        if (client.getMiles() == null || client.getMiles() < milhasParaDebitar) {
            return false;
        }
        client.setMiles(client.getMiles() - milhasParaDebitar);
        clientRepository.save(client);
        return true;
    }
    
    public MilesTransactionDTO getMilesTransactions(int code) {
        Client client = clientRepository.findById(code).orElse(null);
        
        if(client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }

        List<MilesRecord> milesRecords = milesRecordRepository.findByClientCode(code);

        MilesTransactionDTO milesTransactionDTO = new MilesTransactionDTO();

        List<Transaction> transactions = new ArrayList<Transaction>();

        for (MilesRecord milesRecord : milesRecords) {
            Transaction transaction = new Transaction(
                milesRecord.getTransactionDate(),
                milesRecord.getValue(),
                milesRecord.getAmount(),
                milesRecord.getDescription(),
                milesRecord.getBookingCode(),
                milesRecord.getType()
            );

            transactions.add(transaction);
        }

        milesTransactionDTO.setTransacoes(transactions);

        return milesTransactionDTO;
        
    }

    public ClientBookingsDTO getClientBookings(int code){
        Client client = clientRepository.findById(code).orElse(null);

        if(client == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        
        List<MilesRecord> milesRecords = milesRecordRepository.findByClientCode(code);
        List<String> bookingCodes = new ArrayList<String>();

        for (MilesRecord milesRecord : milesRecords) {
            bookingCodes.add(milesRecord.getBookingCode());
        }

        return new ClientBookingsDTO(bookingCodes);

    }
}