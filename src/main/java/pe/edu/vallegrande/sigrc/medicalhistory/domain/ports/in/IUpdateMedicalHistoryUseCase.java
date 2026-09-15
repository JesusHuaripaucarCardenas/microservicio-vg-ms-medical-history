package pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.in;

import pe.edu.vallegrande.sigrc.medicalhistory.domain.models.MedicalHistory;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface IUpdateMedicalHistoryUseCase {
    Mono<MedicalHistory> execute(UUID id, MedicalHistory medicalHistory);
}
