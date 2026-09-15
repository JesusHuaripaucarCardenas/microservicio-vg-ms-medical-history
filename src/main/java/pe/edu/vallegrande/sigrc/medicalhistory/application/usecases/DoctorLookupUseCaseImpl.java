package pe.edu.vallegrande.sigrc.medicalhistory.application.usecases;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.sigrc.medicalhistory.application.dto.response.DoctorLookupResponse;
import pe.edu.vallegrande.sigrc.medicalhistory.application.dto.response.SpecialtyOption;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.in.IDoctorLookupUseCase;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out.IDoctorLookupPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorLookupUseCaseImpl implements IDoctorLookupUseCase {

    private final IDoctorLookupPort doctorLookupPort;

    @Override
    public Flux<DoctorLookupResponse> findByLastName(String lastName) {
        return doctorLookupPort.findByLastNameContaining(lastName)
                .map(this::toLookupResponse);
    }

    @Override
    public Mono<List<SpecialtyOption>> getSpecialties(UUID doctorId) {
        return doctorLookupPort.getSpecialtiesByDoctorId(doctorId);
    }

    private DoctorLookupResponse toLookupResponse(JsonNode d) {
        return DoctorLookupResponse.builder()
                .doctorId(d.has("id") ? UUID.fromString(d.get("id").asText()) : null)
                .firstName(textOrNull(d, "firstName"))
                .lastName(textOrNull(d, "lastName"))
                .motherLastName(textOrNull(d, "motherLastName"))
                .documentType(textOrNull(d, "documentType"))
                .documentNumber(textOrNull(d, "documentNumber"))
                .phone(textOrNull(d, "phone"))
                .build();
    }

    private String textOrNull(JsonNode n, String f) {
        if (n == null || !n.has(f) || n.get(f).isNull()) return null;
        String v = n.get(f).asText();
        return (v == null || v.isBlank()) ? null : v;
    }
}
