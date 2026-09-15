package pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out;

import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * (limpieza) Antes este puerto también exponía getFullNameById(...) y
 * getSpecialtyNamesById(...); ninguna clase los invocaba (ni el caso de uso
 * de creación ni el mapper), por lo que eran código muerto duplicando la
 * misma llamada HTTP que ya hace IDoctorLookupPort para el autocompletado.
 * Se dejó únicamente lo que realmente se usa: la validación de existencia.
 */
public interface IDoctorPort {
    Mono<Boolean> existsById(UUID doctorId);
}
