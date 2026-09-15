package pe.edu.vallegrande.sigrc.medicalhistory.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.exceptions.DomainException;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.exceptions.NotFoundException;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.models.MedicalHistory;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.in.ICreateMedicalHistoryUseCase;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out.IAppointmentPort;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out.IDoctorPort;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out.IMedicalHistoryRepository;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out.IPatientPort;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out.ISpecialtyPort;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateMedicalHistoryUseCaseImpl implements ICreateMedicalHistoryUseCase {

    private final IMedicalHistoryRepository repository;
    private final IPatientPort              patientPort;
    private final IDoctorPort               doctorPort;
    private final IAppointmentPort          appointmentPort;
    private final ISpecialtyPort            specialtyPort;

    private static final String HISTORY_NUMBER_PREFIX = "HC-";
    private static final int    HISTORY_NUMBER_DIGITS  = 8;

    @Override
    public Mono<MedicalHistory> execute(MedicalHistory mh) {
        log.info("Creando historia clínica para paciente: {}", mh.getPatientId());

        // ── Validaciones de campos requeridos ─────────────────────────────────
        if (mh.getDoctorId() == null) {
            return Mono.error(new DomainException("El ID del médico es requerido"));
        }
        // (cambio) La fecha/hora de la consulta ya no la valida ni la envía el
        // cliente: se asigna automáticamente más abajo con LocalDateTime.now().
        if (mh.getSpecialtyId() == null || mh.getSpecialtyId().isBlank()) {
            return Mono.error(new DomainException("El servicio (especialidad) es requerido"));
        }

        // ── Validaciones condicionales ──────────────────────────────────────
        // Si no tiene patientId, se asume que es un registro 100% manual
        // Si tiene patientId, se valida que exista
        Mono<Boolean> patientValid = (mh.getPatientId() != null && !mh.getPatientId().isBlank())
                ? patientPort.existsById(mh.getPatientId())
                : Mono.just(true); // registro manual sin paciente

        // Validar cita solo si viene appointmentId (es opcional)
        Mono<Boolean> appointmentValid = (mh.getAppointmentId() != null && !mh.getAppointmentId().isBlank())
                ? appointmentPort.existsById(mh.getAppointmentId())
                : Mono.just(true);

        // ── Validar paciente, médico, cita y especialidad en paralelo ─────────
        // (mejora) antes solo se validaba paciente/médico/cita; la especialidad
        // (specialtyId) se guardaba sin comprobar que existiera en ms-specialties.
        return Mono.zip(
                patientValid,
                doctorPort.existsById(mh.getDoctorId()),
                appointmentValid,
                specialtyPort.existsById(mh.getSpecialtyId())
        ).flatMap(tuple -> {
            if (!tuple.getT1()) {
                return Mono.error(new NotFoundException(
                        "Paciente no encontrado o inactivo con ID: " + mh.getPatientId()));
            }
            if (!tuple.getT2()) {
                return Mono.error(new NotFoundException(
                        "Médico no encontrado o inactivo con ID: " + mh.getDoctorId()));
            }
            if (!tuple.getT3()) {
                return Mono.error(new NotFoundException(
                        "Cita no encontrada o cancelada con ID: " + mh.getAppointmentId()));
            }
            if (!tuple.getT4()) {
                return Mono.error(new NotFoundException(
                        "Especialidad no encontrada o inactiva con ID: " + mh.getSpecialtyId()));
            }

            mh.setStatus("ACTIVE");
            // (cambio) Fecha/hora de atención 100% automática del servidor,
            // con precisión de minutos/segundos; el cliente ya no la envía
            // ni puede manipularla.
            mh.setVisitDate(LocalDateTime.now());

            // ── Generar N° de historia clínica de forma atómica ──
            return repository.nextHistoryNumberSequence()
                    .map(seq -> {
                        mh.setHistoryNumber(HISTORY_NUMBER_PREFIX +
                                String.format("%0" + HISTORY_NUMBER_DIGITS + "d", seq));
                        return mh;
                    })
                    .flatMap(repository::save);
        }).doOnSuccess(saved -> log.info("Historia clínica creada con id: {} y número: {}",
                saved.getId(), saved.getHistoryNumber()));
    }
}
