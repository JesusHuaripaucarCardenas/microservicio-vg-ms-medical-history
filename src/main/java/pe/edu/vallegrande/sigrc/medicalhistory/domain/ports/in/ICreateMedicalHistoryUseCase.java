package pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.in;

import pe.edu.vallegrande.sigrc.medicalhistory.domain.models.MedicalHistory;
import reactor.core.publisher.Mono;

public interface ICreateMedicalHistoryUseCase {
    Mono<MedicalHistory> execute(MedicalHistory medicalHistory);
}
