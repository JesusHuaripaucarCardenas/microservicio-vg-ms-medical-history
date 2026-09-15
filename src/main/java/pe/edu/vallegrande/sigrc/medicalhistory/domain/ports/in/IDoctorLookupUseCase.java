package pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.in;

import pe.edu.vallegrande.sigrc.medicalhistory.application.dto.response.DoctorLookupResponse;
import pe.edu.vallegrande.sigrc.medicalhistory.application.dto.response.SpecialtyOption;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para autocompletar el formulario de Historia Clínica buscando
 * al médico por apellido y obteniendo sus especialidades asignadas.
 */
public interface IDoctorLookupUseCase {

    /** Búsqueda parcial por apellido (case-insensitive), solo médicos ACTIVE. */
    Flux<DoctorLookupResponse> findByLastName(String lastName);

    /** Especialidades (id + nombre) asignadas a un médico específico. */
    Mono<List<SpecialtyOption>> getSpecialties(UUID doctorId);
}
