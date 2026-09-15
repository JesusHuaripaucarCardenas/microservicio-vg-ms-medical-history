package pe.edu.vallegrande.sigrc.medicalhistory.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.models.MedicalHistory;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.in.IDeleteMedicalHistoryUseCase;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out.IMedicalHistoryRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteMedicalHistoryUseCaseImpl implements IDeleteMedicalHistoryUseCase {

    private final IMedicalHistoryRepository repository;

    @Override
    public Mono<MedicalHistory> deactivate(UUID id) {
        log.info("Desactivando historia clínica con id: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Historia clínica no encontrada con id: " + id)))
                .flatMap(mh -> {
                    mh.setStatus("INACTIVE");
                    return repository.save(mh);
                })
                .doOnSuccess(mh -> log.info("Historia clínica desactivada con id: {}", mh.getId()));
    }

    @Override
    public Mono<MedicalHistory> restore(UUID id) {
        log.info("Restaurando historia clínica con id: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Historia clínica no encontrada con id: " + id)))
                .flatMap(mh -> {
                    mh.setStatus("ACTIVE");
                    return repository.save(mh);
                })
                .doOnSuccess(mh -> log.info("Historia clínica restaurada con id: {}", mh.getId()));
    }
}
