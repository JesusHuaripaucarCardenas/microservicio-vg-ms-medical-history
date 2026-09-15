package pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out;

import pe.edu.vallegrande.sigrc.medicalhistory.domain.models.MedicalHistory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface IMedicalHistoryRepository {
    Mono<MedicalHistory> save(MedicalHistory medicalHistory);
    Mono<MedicalHistory> findById(UUID id);
    Flux<MedicalHistory> findAll();
    Flux<MedicalHistory> findByStatus(String status);
    Flux<MedicalHistory> findByPatientId(String patientId);
    Flux<MedicalHistory> findByDoctorId(UUID doctorId);
    Flux<MedicalHistory> findByAppointmentId(String appointmentId);
    Flux<MedicalHistory> findByVisitDateBetween(LocalDate start, LocalDate end);
    Flux<MedicalHistory> findByPatientIdAndStatus(String patientId, String status);
    Mono<Boolean> existsById(UUID id);

    /** Próximo correlativo para historyNumber (HC-00000001, HC-00000002, ...). Atómico vía secuencia en BD. */
    Mono<Long> nextHistoryNumberSequence();
}
