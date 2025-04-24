package backend.clients.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.clients.models.MilesRecord;

import java.util.Date;
import java.util.List;

public interface MilesRecordRepository extends JpaRepository<MilesRecord, String> {
    List<MilesRecord> findByClientCpf(String clientCpf);
    MilesRecord findByClientCpfAndTransactionDate(String clientCpf, Date transactionDate);
}
