package pe.edu.vallegrande.sigrc.medicalhistory.infrastructure.adapters.in.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.sigrc.medicalhistory.application.dto.common.ApiResponse;
import pe.edu.vallegrande.sigrc.medicalhistory.application.dto.request.CreateMedicalHistoryRequest;
import pe.edu.vallegrande.sigrc.medicalhistory.application.dto.request.UpdateMedicalHistoryRequest;
import pe.edu.vallegrande.sigrc.medicalhistory.application.dto.response.MedicalHistoryResponse;
import pe.edu.vallegrande.sigrc.medicalhistory.application.dto.response.PatientLookupResponse;
import pe.edu.vallegrande.sigrc.medicalhistory.application.dto.response.DoctorLookupResponse;
import pe.edu.vallegrande.sigrc.medicalhistory.application.dto.response.SpecialtyOption;
import pe.edu.vallegrande.sigrc.medicalhistory.application.mappers.MedicalHistoryMapper;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.models.MedicalHistory;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.in.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/medical-histories")
@RequiredArgsConstructor
public class MedicalHistoryController {

    private final ICreateMedicalHistoryUseCase createUseCase;
    private final IGetMedicalHistoryUseCase getUseCase;
    private final IUpdateMedicalHistoryUseCase updateUseCase;
    private final IDeleteMedicalHistoryUseCase deleteUseCase;
    private final IPatientLookupUseCase patientLookupUseCase;
    private final IDoctorLookupUseCase doctorLookupUseCase;
    private final MedicalHistoryMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Mono<ApiResponse<MedicalHistoryResponse>> create(
            @Valid @RequestBody CreateMedicalHistoryRequest req) {
        log.info("📥 Recibiendo solicitud de creación: {}", req);
        MedicalHistory domain = mapper.toDomain(req);
        return createUseCase.execute(domain)
                .map(mapper::toResponse)
                .map(r -> ApiResponse.<MedicalHistoryResponse>builder()
                        .success(true)
                        .message("Historia clínica creada exitosamente")
                        .data(r)
                        .build())
                .doOnSuccess(res -> log.info("✅ Historia creada: {}", res))
                .doOnError(err -> log.error("❌ Error al crear: {}", err.getMessage()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Mono<ApiResponse<List<MedicalHistoryResponse>>> findAll(
            @RequestParam(required = false) String status) {
        Flux<MedicalHistory> source = status != null ? getUseCase.findByStatus(status) : getUseCase.findAll();
        return source.map(mapper::toResponse)
                .collectList()
                .map(list -> ApiResponse.<List<MedicalHistoryResponse>>builder()
                        .success(true)
                        .message("Historias clínicas listadas")
                        .data(list)
                        .build());
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Mono<ApiResponse<List<MedicalHistoryResponse>>> findByPatientId(
            @PathVariable String patientId,
            @RequestParam(required = false) String status) {
        Flux<MedicalHistory> source = status != null
                ? getUseCase.findByPatientIdAndStatus(patientId, status)
                : getUseCase.findByPatientId(patientId);
        return source.map(mapper::toResponse)
                .collectList()
                .map(list -> ApiResponse.<List<MedicalHistoryResponse>>builder()
                        .success(true)
                        .message("Historias clínicas del paciente")
                        .data(list)
                        .build());
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Mono<ApiResponse<List<MedicalHistoryResponse>>> findByDoctorId(@PathVariable UUID doctorId) {
        return getUseCase.findByDoctorId(doctorId).map(mapper::toResponse)
                .collectList()
                .map(list -> ApiResponse.<List<MedicalHistoryResponse>>builder()
                        .success(true)
                        .message("Historias clínicas del médico")
                        .data(list)
                        .build());
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Mono<ApiResponse<List<MedicalHistoryResponse>>> findByAppointmentId(@PathVariable String appointmentId) {
        return getUseCase.findByAppointmentId(appointmentId).map(mapper::toResponse)
                .collectList()
                .map(list -> ApiResponse.<List<MedicalHistoryResponse>>builder()
                        .success(true)
                        .message("Historias clínicas de la cita")
                        .data(list)
                        .build());
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Mono<ApiResponse<List<MedicalHistoryResponse>>> findByDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        return getUseCase.findByVisitDateBetween(start, end).map(mapper::toResponse)
                .collectList()
                .map(list -> ApiResponse.<List<MedicalHistoryResponse>>builder()
                        .success(true)
                        .message("Historias clínicas en el rango de fechas")
                        .data(list)
                        .build());
    }

    // ── AUTOCOMPLETADO DE PACIENTE ───────────────────────────────────────────

    /**
     * NUEVO: búsqueda SOLO por número de documento, sin tener que elegir el
     * tipo primero. El backend detecta el tipo real (DNI/CE/CIE/...) y lo
     * devuelve en la respuesta, para que el frontend lo autocomplete también.
     * Es el endpoint que usa el wizard de "Nueva historia clínica".
     */
    @GetMapping("/patient-lookup/document/{documentNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Mono<ApiResponse<PatientLookupResponse>> lookupPatientByDocumentNumber(
            @PathVariable String documentNumber) {
        return patientLookupUseCase.findByDocumentNumber(documentNumber)
                .map(r -> ApiResponse.<PatientLookupResponse>builder()
                        .success(true)
                        .message("Paciente encontrado")
                        .data(r)
                        .build());
    }

    /** Variante explícita cuando ya se conoce el tipo de documento. */
    @GetMapping("/patient-lookup/document/{documentType}/{documentNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Mono<ApiResponse<PatientLookupResponse>> lookupPatientByDocument(
            @PathVariable String documentType,
            @PathVariable String documentNumber) {
        return patientLookupUseCase.findByDocument(documentType, documentNumber)
                .map(r -> ApiResponse.<PatientLookupResponse>builder()
                        .success(true)
                        .message("Paciente encontrado")
                        .data(r)
                        .build());
    }

    @GetMapping("/patient-lookup/lastname/{lastName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Flux<PatientLookupResponse> lookupPatientsByLastName(@PathVariable String lastName) {
        return patientLookupUseCase.findByLastName(lastName);
    }

    @GetMapping("/patient-lookup/id/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Mono<ApiResponse<PatientLookupResponse>> lookupPatientById(@PathVariable String patientId) {
        return patientLookupUseCase.findByPatientId(patientId)
                .map(r -> ApiResponse.<PatientLookupResponse>builder()
                        .success(true)
                        .message("Paciente encontrado")
                        .data(r)
                        .build());
    }

    // (cambio) Se retiró el endpoint POST /patient-lookup/create: ya no se
    // crean pacientes desde ms-medical-history. Si no existe, se registra
    // desde el módulo de Pacientes (ms-patients) y luego se vuelve a buscar.

    // ── AUTOCOMPLETADO DE MÉDICO ──────────────────────────────────────────────
    @GetMapping("/doctor-lookup/lastname/{lastName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Flux<DoctorLookupResponse> lookupDoctorsByLastName(@PathVariable String lastName) {
        return doctorLookupUseCase.findByLastName(lastName);
    }

    @GetMapping("/doctor-lookup/{doctorId}/specialties")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Mono<ApiResponse<List<SpecialtyOption>>> getDoctorSpecialties(@PathVariable UUID doctorId) {
        return doctorLookupUseCase.getSpecialties(doctorId)
                .map(list -> ApiResponse.<List<SpecialtyOption>>builder()
                        .success(true)
                        .message("Especialidades del médico")
                        .data(list)
                        .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Mono<ApiResponse<MedicalHistoryResponse>> findById(@PathVariable UUID id) {
        return getUseCase.findById(id)
                .map(mapper::toResponse)
                .map(r -> ApiResponse.<MedicalHistoryResponse>builder()
                        .success(true)
                        .message("Historia clínica encontrada")
                        .data(r)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public Mono<ApiResponse<MedicalHistoryResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMedicalHistoryRequest req) {
        return updateUseCase.execute(id, mapper.toDomain(req))
                .map(mapper::toResponse)
                .map(r -> ApiResponse.<MedicalHistoryResponse>builder()
                        .success(true)
                        .message("Historia clínica actualizada exitosamente")
                        .data(r)
                        .build());
    }

    @PatchMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ApiResponse<MedicalHistoryResponse>> deactivate(@PathVariable UUID id) {
        return deleteUseCase.deactivate(id)
                .map(mapper::toResponse)
                .map(r -> ApiResponse.<MedicalHistoryResponse>builder()
                        .success(true)
                        .message("Historia clínica desactivada exitosamente")
                        .data(r)
                        .build());
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ApiResponse<MedicalHistoryResponse>> restore(@PathVariable UUID id) {
        return deleteUseCase.restore(id)
                .map(mapper::toResponse)
                .map(r -> ApiResponse.<MedicalHistoryResponse>builder()
                        .success(true)
                        .message("Historia clínica restaurada exitosamente")
                        .data(r)
                        .build());
    }
}