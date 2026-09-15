package pe.edu.vallegrande.sigrc.medicalhistory.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.models.MedicalHistory;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.in.IUpdateMedicalHistoryUseCase;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out.IMedicalHistoryRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateMedicalHistoryUseCaseImpl implements IUpdateMedicalHistoryUseCase {

    private final IMedicalHistoryRepository repository;

    @Override
    public Mono<MedicalHistory> execute(UUID id, MedicalHistory update) {
        log.info("Actualizando historia clínica con id: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Historia clínica no encontrada con id: " + id)))
                .flatMap(existing -> {
                    if (update.getPatientId()             != null) existing.setPatientId(update.getPatientId());
                    if (update.getPatientFirstName()       != null) existing.setPatientFirstName(update.getPatientFirstName());
                    if (update.getPatientLastName()        != null) existing.setPatientLastName(update.getPatientLastName());
                    if (update.getPatientDocumentType()    != null) existing.setPatientDocumentType(update.getPatientDocumentType());
                    if (update.getPatientDocumentNumber()  != null) existing.setPatientDocumentNumber(update.getPatientDocumentNumber());
                    if (update.getPatientGender()          != null) existing.setPatientGender(update.getPatientGender());
                    if (update.getPatientBirthDate()       != null) existing.setPatientBirthDate(update.getPatientBirthDate());
                    if (update.getPatientDistrict()        != null) existing.setPatientDistrict(update.getPatientDistrict());
                    if (update.getPatientProvince()        != null) existing.setPatientProvince(update.getPatientProvince());
                    if (update.getPatientDepartment()      != null) existing.setPatientDepartment(update.getPatientDepartment());
                    if (update.getPatientMaritalStatus()   != null) existing.setPatientMaritalStatus(update.getPatientMaritalStatus());
                    if (update.getPatientPhone()           != null) existing.setPatientPhone(update.getPatientPhone());
                    if (update.getDoctorId()               != null) existing.setDoctorId(update.getDoctorId());
                    if (update.getDoctorFirstName()        != null) existing.setDoctorFirstName(update.getDoctorFirstName());
                    if (update.getDoctorLastName()         != null) existing.setDoctorLastName(update.getDoctorLastName());
                    if (update.getDoctorMothersLastName()  != null) existing.setDoctorMothersLastName(update.getDoctorMothersLastName());
                    if (update.getDoctorDocumentType()     != null) existing.setDoctorDocumentType(update.getDoctorDocumentType());
                    if (update.getDoctorDocumentNumber()   != null) existing.setDoctorDocumentNumber(update.getDoctorDocumentNumber());
                    if (update.getDoctorPhone()            != null) existing.setDoctorPhone(update.getDoctorPhone());
                    if (update.getAppointmentId()          != null) existing.setAppointmentId(update.getAppointmentId());
                    // (cambio) La fecha/hora de la consulta no se puede editar.
                    if (update.getSpecialtyId()      != null) existing.setSpecialtyId(update.getSpecialtyId());
                    if (update.getSpecialtyName()    != null) existing.setSpecialtyName(update.getSpecialtyName());
                    if (update.getBirthPlace()       != null) existing.setBirthPlace(update.getBirthPlace());
                    if (update.getOccupation()       != null) existing.setOccupation(update.getOccupation());
                    if (update.getDiagnosis()        != null) existing.setDiagnosis(update.getDiagnosis());
                    if (update.getCie10Code()        != null) existing.setCie10Code(update.getCie10Code());
                    if (update.getAnamnesis()        != null) existing.setAnamnesis(update.getAnamnesis());
                    if (update.getBackground()       != null) existing.setBackground(update.getBackground());
                    if (update.getMedicalEvaluation()!= null) existing.setMedicalEvaluation(update.getMedicalEvaluation());
                    if (update.getObservations()     != null) existing.setObservations(update.getObservations());
                    return repository.save(existing);
                })
                .doOnSuccess(mh -> log.info("Historia clínica actualizada con id: {}", mh.getId()));
    }
}
