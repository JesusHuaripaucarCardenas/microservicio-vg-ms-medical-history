package pe.edu.vallegrande.sigrc.medicalhistory.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.models.MedicalHistory;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.in.IGetMedicalHistoryUseCase;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out.IMedicalHistoryRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetMedicalHistoryUseCaseImpl implements IGetMedicalHistoryUseCase {

    private final IMedicalHistoryRepository repository;

    @Override
    public Mono<MedicalHistory> findById(UUID id) {
        log.info("Buscando historia clínica con id: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Historia clínica no encontrada con id: " + id)));
    }

    @Override
    public Flux<MedicalHistory> findAll() {
        log.info("Listando todas las historias clínicas");
        return repository.findAll();
    }

    @Override
    public Flux<MedicalHistory> findByStatus(String status) {
        log.info("Listando historias clínicas por estado: {}", status);
        return repository.findByStatus(status);
    }

    @Override
    public Flux<MedicalHistory> findByPatientId(String patientId) {
        log.info("Listando historias clínicas del paciente: {}", patientId);
        return repository.findByPatientId(patientId);
    }

    @Override
    public Flux<MedicalHistory> findByDoctorId(UUID doctorId) {
        log.info("Listando historias clínicas del médico: {}", doctorId);
        return repository.findByDoctorId(doctorId);
    }

    @Override
    public Flux<MedicalHistory> findByAppointmentId(String appointmentId) {
        log.info("Listando historias clínicas de la cita: {}", appointmentId);
        return repository.findByAppointmentId(appointmentId);
    }

    @Override
    public Flux<MedicalHistory> findByVisitDateBetween(LocalDate start, LocalDate end) {
        log.info("Listando historias clínicas entre {} y {}", start, end);
        return repository.findByVisitDateBetween(start, end);
    }

    @Override
    public Flux<MedicalHistory> findByPatientIdAndStatus(String patientId, String status) {
        log.info("Listando historias del paciente {} con estado {}", patientId, status);
        return repository.findByPatientIdAndStatus(patientId, status);
    }
}
