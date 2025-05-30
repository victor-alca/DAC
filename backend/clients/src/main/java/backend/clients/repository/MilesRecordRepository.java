package backend.clients.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.clients.models.MilesRecord;
import backend.clients.models.MilesRecordId;

import java.util.List;

public interface MilesRecordRepository extends JpaRepository<MilesRecord, MilesRecordId> {
    List<MilesRecord> findByClientCode(int clientCode);
}
