package pe.edu.vallegrande.sigrc.medicalhistory.infrastructure.adapters.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface MedicalHistoryR2dbcRepository
        extends ReactiveCrudRepository<MedicalHistoryDocument, UUID> {

    Flux<MedicalHistoryDocument> findByStatus(String status);
    Flux<MedicalHistoryDocument> findByPatientId(String patientId);
    Flux<MedicalHistoryDocument> findByDoctorId(UUID doctorId);
    Flux<MedicalHistoryDocument> findByAppointmentId(String appointmentId);
    Flux<MedicalHistoryDocument> findByPatientIdAndStatus(String patientId, String status);
}
