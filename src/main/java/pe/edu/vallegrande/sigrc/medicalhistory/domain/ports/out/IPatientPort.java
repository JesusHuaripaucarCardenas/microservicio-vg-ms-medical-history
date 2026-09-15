package pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * (cambio) Este puerto ya NO crea pacientes. ms-medical-history solo lee de
 * ms-patients; si el paciente no existe, la historia clínica no se guarda y
 * se le indica al usuario que lo registre desde el módulo de Pacientes.
 */
public interface IPatientPort {

    Mono<Boolean> existsById(String patientId);

    Mono<JsonNode> findByDocumentTypeAndDocumentNumber(String documentType, String documentNumber);

    /**
     * Busca un paciente por número de documento SIN conocer el tipo de
     * antemano: permite que en el formulario de Historia Clínica el usuario
     * solo escriba el número y el sistema detecte automáticamente si es
     * DNI, CE, etc.
     */
    Mono<JsonNode> findByDocumentNumberOnly(String documentNumber);

    Flux<JsonNode> findByLastNameContaining(String lastName);

    Mono<JsonNode> getPatientById(String patientId);
}
