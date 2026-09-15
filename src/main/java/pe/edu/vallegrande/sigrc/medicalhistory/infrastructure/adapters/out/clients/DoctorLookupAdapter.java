package pe.edu.vallegrande.sigrc.medicalhistory.infrastructure.adapters.out.clients;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.sigrc.medicalhistory.application.dto.response.SpecialtyOption;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out.IDoctorLookupPort;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out.ISpecialtyPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class DoctorLookupAdapter implements IDoctorLookupPort {

    private final WebClient webClient;
    private final String doctorsUrl;
    private final ISpecialtyPort specialtyPort;

    public DoctorLookupAdapter(
            WebClient.Builder builder,
            @Value("${services.doctors.url:https://lab.vallegrande.edu.pe/sigrc/gateway/api/v1/doctors}") String doctorsUrl,
            ISpecialtyPort specialtyPort) {
        this.webClient = builder.build();
        this.doctorsUrl = doctorsUrl;
        this.specialtyPort = specialtyPort;
        // Diagnóstico: si ves "Failed to resolve 'http'" en los logs, revisa
        // que este valor sea una URL completa (https://...) y no algo como
        // "http" a secas; también revisa variables de entorno de proxy
        // (HTTP_PROXY/http_proxy) que puedan estar mal configuradas.
        log.info("DoctorLookupAdapter usando doctors.url = {}", doctorsUrl);
    }

    @Override
    public Flux<JsonNode> findByLastNameContaining(String lastName) {
        String needle = lastName == null ? "" : lastName.trim().toLowerCase();
        return webClient.get()
                .uri(doctorsUrl + "?status=ACTIVE")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMapMany(response -> {
                    JsonNode data = response.has("data") ? response.get("data") : response;
                    return data != null && data.isArray() ? Flux.fromIterable(data) : Flux.empty();
                })
                .filter(doc -> matchesLastName(doc, needle))
                .onErrorResume(err -> {
                    log.warn("No se pudo buscar médicos por apellido: {}", err.getMessage());
                    return Flux.empty();
                });
    }

    @Override
    public Mono<JsonNode> getDoctorById(UUID doctorId) {
        return webClient.get()
                .uri(doctorsUrl + "/" + doctorId)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> response.has("data") ? response.get("data") : response)
                .onErrorResume(err -> Mono.empty());
    }

    @Override
    public Mono<List<SpecialtyOption>> getSpecialtiesByDoctorId(UUID doctorId) {
        return getDoctorById(doctorId)
                .flatMap(doctor -> {
                    if (!doctor.has("specialtyId") || doctor.get("specialtyId").isNull()) {
                        return Mono.just(List.<SpecialtyOption>of());
                    }
                    String specialtyId = doctor.get("specialtyId").asText();
                    if (specialtyId.isBlank()) {
                        return Mono.just(List.<SpecialtyOption>of());
                    }
                    return specialtyPort.getNameById(specialtyId)
                            .map(name -> List.of(SpecialtyOption.builder()
                                    .id(specialtyId)
                                    .name(name)
                                    .build()));
                })
                .defaultIfEmpty(List.of());
    }

    // El campo "Apellidos" del formulario (Apellido paterno y materno) manda
    // ambos apellidos juntos, p.ej. "Sliva Sanchez". El doctor solo guarda
    // lastName y motherLastName por separado, así que comparar needle contra
    // lastName a secas nunca calzaba con una búsqueda de dos palabras.
    // Se exige que CADA palabra del needle aparezca en "lastName motherLastName".
    private boolean matchesLastName(JsonNode doc, String needle) {
        if (needle.isBlank()) return true;
        String lastName = doc.has("lastName") ? doc.get("lastName").asText("") : "";
        String motherLastName = doc.has("motherLastName") ? doc.get("motherLastName").asText("") : "";
        String combined = (lastName + " " + motherLastName).toLowerCase();
        for (String word : needle.split("\\s+")) {
            if (!combined.contains(word)) {
                return false;
            }
        }
        return true;
    }
}
