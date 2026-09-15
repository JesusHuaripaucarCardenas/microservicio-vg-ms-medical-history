package pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IDoctorLookupPort {

    /** Búsqueda parcial por apellido (case-insensitive), solo doctores ACTIVE. */
    Flux<JsonNode> findByLastNameContaining(String lastName);

    /** Doctor completo por ID, incluyendo specialtyIds. */
    Mono<JsonNode> getDoctorById(java.util.UUID doctorId);

    /** Nombres + ids de las especialidades de un doctor (resueltas vía ms-specialties). */
    Mono<java.util.List<pe.edu.vallegrande.sigrc.medicalhistory.application.dto.response.SpecialtyOption>>
        getSpecialtiesByDoctorId(java.util.UUID doctorId);
}
