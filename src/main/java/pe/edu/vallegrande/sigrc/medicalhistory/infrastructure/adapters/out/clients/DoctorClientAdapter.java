package pe.edu.vallegrande.sigrc.medicalhistory.infrastructure.adapters.out.clients;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out.IDoctorPort;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * (limpieza) Este adaptador solo valida existencia del médico. Antes también
 * resolvía nombre completo y especialidades duplicando la lógica que ya
 * vive en DoctorLookupAdapter (usada por el autocompletado); esos métodos
 * no los llamaba nadie, así que se retiraron junto con la dependencia
 * innecesaria a ISpecialtyPort.
 */
@Slf4j
@Component
public class DoctorClientAdapter implements IDoctorPort {

    private final WebClient webClient;
    private final String    doctorsUrl;

    public DoctorClientAdapter(
            WebClient.Builder builder,
            @Value("${services.doctors.url:https://lab.vallegrande.edu.pe/sigrc/gateway/api/v1/doctors}") String doctorsUrl) {
        this.webClient  = builder.build();
        this.doctorsUrl = doctorsUrl;
    }

    @Override
    public Mono<Boolean> existsById(UUID doctorId) {
        String url = doctorsUrl + "/" + doctorId;
        log.info("Validando médico en: {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    JsonNode data = response.has("data") ? response.get("data") : response;
                    String status = data.has("status") ? data.get("status").asText() : null;
                    return "ACTIVE".equals(status);
                })
                .onErrorResume(err -> {
                    log.warn("No se pudo contactar ms-doctors (tolerancia a fallos): {}", err.getMessage());
                    return Mono.just(true);
                });
    }
}
