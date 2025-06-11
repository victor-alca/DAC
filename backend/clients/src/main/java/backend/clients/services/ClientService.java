package backend.clients.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.clients.dto.ClientBookingsDTO;
import backend.clients.dto.ClientDTO;
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

    public Client addClient(ClientDTO newClient) {

        if(clientRepository.findByCpf(newClient.cpf) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O Cliente já existe!");
        }
        if(clientRepository.findByEmail(newClient.email) != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O Cliente já existe!");
        }

        Client clientToSave = new Client();
        clientToSave.setCpf(newClient.cpf);
        clientToSave.setEmail(newClient.email);
        clientToSave.setName(newClient.nome);
        clientToSave.setMiles((double) newClient.saldo_milhas);
        clientToSave.setCep(newClient.endereco.cep);
        clientToSave.setFederativeUnit(newClient.endereco.uf);
        clientToSave.setCity(newClient.endereco.cidade);
        clientToSave.setNeighborhood(newClient.endereco.bairro);
        clientToSave.setStreet(newClient.endereco.rua);
        clientToSave.setNumber(newClient.endereco.numero);
        clientToSave.setComplement(newClient.endereco.complemento);
        
        clientRepository.save(clientToSave);

        return clientToSave;
    }

    public Client getClient (int code) {
        Client client = clientRepository.findById(code).orElse(null);
        if(client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        return client;
    }

    public Client getClientByEmail (String email) {
        Client client = clientRepository.findByEmail(email);
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

    public MilesBalanceDTO addMiles(int clientCode, Double miles) {
        Client client = clientRepository.findById(clientCode).orElse(null);
        if(client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        client.setMiles(client.getMiles() + miles);
        clientRepository.save(client);
        
        MilesBalanceDTO milesBalanceDTO = new MilesBalanceDTO(clientCode, client.getMiles());
        return milesBalanceDTO;
    }

    public MilesBalanceDTO debitMiles(int clientCode, Double miles) {
        Client client = clientRepository.findById(clientCode).orElse(null);
        if (client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }
        if (client.getMiles() == null || client.getMiles() < miles) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Milhas insuficientes para a operação");
        }
        
        client.setMiles(client.getMiles() - miles);
        clientRepository.save(client);
        
        MilesBalanceDTO milesBalanceDTO = new MilesBalanceDTO(clientCode, client.getMiles());
        return milesBalanceDTO;
    }

    public void recordMilesTransaction(int clientCode, Double miles, String type, String description, String bookingCode) {
        Client client = clientRepository.findById(clientCode).orElse(null);
        if(client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        
        MilesRecord milesRecord = new MilesRecord();
        milesRecord.setClientCode(clientCode);
        milesRecord.setTransactionDate(new Timestamp(System.currentTimeMillis()));
        milesRecord.setClient(client);
        milesRecord.setValue(miles.intValue() * 5);
        milesRecord.setAmount(miles.intValue());
        milesRecord.setType(type);
        milesRecord.setDescription(description);
        milesRecord.setBookingCode(bookingCode != null ? bookingCode : "");
        
        milesRecordRepository.save(milesRecord);
    }

    public void recordMilesDebit(int clientCode, Double miles, String description, String bookingCode) {
        Client client = clientRepository.findById(clientCode).orElse(null);
        if(client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        
        MilesRecord milesRecord = new MilesRecord();
        milesRecord.setClientCode(clientCode);
        milesRecord.setTransactionDate(new Timestamp(System.currentTimeMillis()));
        milesRecord.setClient(client);
        milesRecord.setValue((int) (miles * 5));
        milesRecord.setAmount((int) -miles);
        milesRecord.setType("SAIDA");
        milesRecord.setDescription(description != null ? description : "");
        milesRecord.setBookingCode(bookingCode);
        
        milesRecordRepository.save(milesRecord);
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