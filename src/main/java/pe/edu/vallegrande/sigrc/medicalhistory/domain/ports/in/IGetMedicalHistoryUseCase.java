package pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.in;

import pe.edu.vallegrande.sigrc.medicalhistory.domain.models.MedicalHistory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

public interface IGetMedicalHistoryUseCase {
    Mono<MedicalHistory> findById(UUID id);
    Flux<MedicalHistory> findAll();
    Flux<MedicalHistory> findByStatus(String status);
    Flux<MedicalHistory> findByPatientId(String patientId);
    Flux<MedicalHistory> findByDoctorId(UUID doctorId);
    Flux<MedicalHistory> findByAppointmentId(String appointmentId);
    Flux<MedicalHistory> findByVisitDateBetween(LocalDate start, LocalDate end);
    Flux<MedicalHistory> findByPatientIdAndStatus(String patientId, String status);
}
