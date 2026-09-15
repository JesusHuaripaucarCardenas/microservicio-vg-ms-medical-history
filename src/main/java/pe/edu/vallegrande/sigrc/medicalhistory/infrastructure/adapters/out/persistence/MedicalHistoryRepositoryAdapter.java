package pe.edu.vallegrande.sigrc.medicalhistory.infrastructure.adapters.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.models.MedicalHistory;
import pe.edu.vallegrande.sigrc.medicalhistory.domain.ports.out.IMedicalHistoryRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicalHistoryRepositoryAdapter implements IMedicalHistoryRepository {

    private final MedicalHistoryR2dbcRepository r2dbcRepository;
    private final DatabaseClient db;

    @Override
    public Mono<MedicalHistory> save(MedicalHistory mh) {
        boolean isNew = mh.getId() == null;
        log.info("Guardando historia clinica - isNew: {}, id: {}, historyNumber: {}",
                isNew, mh.getId(), mh.getHistoryNumber());
        return isNew ? insert(mh) : update(mh);
    }

    private DatabaseClient.GenericExecuteSpec bindNullableString(
            DatabaseClient.GenericExecuteSpec spec, String name, String value) {
        if (value == null || value.isBlank()) {
            return spec.bindNull(name, String.class);
        }
        return spec.bind(name, value);
    }

    private DatabaseClient.GenericExecuteSpec bindNullableUuid(
            DatabaseClient.GenericExecuteSpec spec, String name, UUID value) {
        if (value == null) {
            return spec.bindNull(name, UUID.class);
        }
        return spec.bind(name, value);
    }

    private DatabaseClient.GenericExecuteSpec bindNullableDate(
            DatabaseClient.GenericExecuteSpec spec, String name, LocalDate value) {
        if (value == null) {
            return spec.bindNull(name, LocalDate.class);
        }
        return spec.bind(name, value);
    }

    private DatabaseClient.GenericExecuteSpec bindNullableDateTime(
            DatabaseClient.GenericExecuteSpec spec, String name, LocalDateTime value) {
        if (value == null) {
            return spec.bindNull(name, LocalDateTime.class);
        }
        return spec.bind(name, value);
    }

    // ════════════════════════════════════════════════════════════════════════
    // INSERT
    // ════════════════════════════════════════════════════════════════════════
    private Mono<MedicalHistory> insert(MedicalHistory mh) {
        UUID newId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        log.info("Insertando nueva historia clinica con ID: {}", newId);

        String sql = "INSERT INTO medical_histories ( "
                + "id, history_number, "
                + "patient_id, doctor_id, appointment_id, "
                + "patient_first_name, patient_last_name, "
                + "patient_document_type, patient_document_number, "
                + "patient_gender, patient_birth_date, "
                + "patient_district, patient_province, patient_department, "
                + "patient_marital_status, patient_phone, "
                + "doctor_first_name, doctor_last_name, doctor_mothers_last_name, "
                + "doctor_document_type, doctor_document_number, doctor_phone, "
                + "specialty_id, specialty_name, "
                + "visit_date, birth_place, occupation, "
                + "diagnosis, cie10_code, anamnesis, background, "
                + "medical_evaluation, observations, "
                + "status, created_at, updated_at "
                + ") VALUES ( "
                + ":id, :historyNumber, "
                + ":patientId, :doctorId, :appointmentId, "
                + ":patientFirstName, :patientLastName, "
                + ":patientDocumentType, :patientDocumentNumber, "
                + ":patientGender, :patientBirthDate, "
                + ":patientDistrict, :patientProvince, :patientDepartment, "
                + ":patientMaritalStatus, :patientPhone, "
                + ":doctorFirstName, :doctorLastName, :doctorMothersLastName, "
                + ":doctorDocumentType, :doctorDocumentNumber, :doctorPhone, "
                + ":specialtyId, :specialtyName, "
                + ":visitDate, :birthPlace, :occupation, "
                + ":diagnosis, :cie10Code, :anamnesis, :background, "
                + ":medicalEvaluation, :observations, "
                + ":status, :createdAt, :updatedAt "
                + ") RETURNING *";

        DatabaseClient.GenericExecuteSpec spec = db.sql(sql)
                .bind("id", newId)
                .bind("historyNumber", mh.getHistoryNumber());

        spec = bindNullableString(spec, "patientId", mh.getPatientId());
        spec = bindNullableUuid(spec, "doctorId", mh.getDoctorId());
        spec = bindNullableString(spec, "appointmentId", mh.getAppointmentId());

        spec = bindNullableString(spec, "patientFirstName", mh.getPatientFirstName());
        spec = bindNullableString(spec, "patientLastName", mh.getPatientLastName());
        spec = bindNullableString(spec, "patientDocumentType", mh.getPatientDocumentType());
        spec = bindNullableString(spec, "patientDocumentNumber", mh.getPatientDocumentNumber());
        spec = bindNullableString(spec, "patientGender", mh.getPatientGender());
        spec = bindNullableDate(spec, "patientBirthDate", mh.getPatientBirthDate());
        spec = bindNullableString(spec, "patientDistrict", mh.getPatientDistrict());
        spec = bindNullableString(spec, "patientProvince", mh.getPatientProvince());
        spec = bindNullableString(spec, "patientDepartment", mh.getPatientDepartment());
        spec = bindNullableString(spec, "patientMaritalStatus", mh.getPatientMaritalStatus());
        spec = bindNullableString(spec, "patientPhone", mh.getPatientPhone());

        spec = bindNullableString(spec, "doctorFirstName", mh.getDoctorFirstName());
        spec = bindNullableString(spec, "doctorLastName", mh.getDoctorLastName());
        spec = bindNullableString(spec, "doctorMothersLastName", mh.getDoctorMothersLastName());
        spec = bindNullableString(spec, "doctorDocumentType", mh.getDoctorDocumentType());
        spec = bindNullableString(spec, "doctorDocumentNumber", mh.getDoctorDocumentNumber());
        spec = bindNullableString(spec, "doctorPhone", mh.getDoctorPhone());
        spec = bindNullableString(spec, "specialtyId", mh.getSpecialtyId());
        spec = bindNullableString(spec, "specialtyName", mh.getSpecialtyName());

        spec = bindNullableDateTime(spec, "visitDate", mh.getVisitDate());
        spec = bindNullableString(spec, "birthPlace", mh.getBirthPlace());
        spec = bindNullableString(spec, "occupation", mh.getOccupation());
        spec = bindNullableString(spec, "diagnosis", mh.getDiagnosis());
        spec = bindNullableString(spec, "cie10Code", mh.getCie10Code());
        spec = bindNullableString(spec, "anamnesis", mh.getAnamnesis());
        spec = bindNullableString(spec, "background", mh.getBackground());
        spec = bindNullableString(spec, "medicalEvaluation", mh.getMedicalEvaluation());
        spec = bindNullableString(spec, "observations", mh.getObservations());

        spec = spec.bind("status", mh.getStatus() != null ? mh.getStatus() : "ACTIVE")
                .bind("createdAt", now)
                .bind("updatedAt", now);

        return spec
                .map((row, rowMetadata) -> rowToDocument(row))
                .one()
                .map(this::toModel)
                .doOnSuccess(saved -> log.info("Historia clinica insertada: id={}, historyNumber={}",
                        saved.getId(), saved.getHistoryNumber()))
                .doOnError(err -> log.error("Error al insertar historia clinica: {}", err.getMessage()));
    }

    // ════════════════════════════════════════════════════════════════════════
    // UPDATE
    // ════════════════════════════════════════════════════════════════════════
    private Mono<MedicalHistory> update(MedicalHistory mh) {
        LocalDateTime now = LocalDateTime.now();

        log.info("Actualizando historia clinica con ID: {}", mh.getId());

        String sql = "UPDATE medical_histories SET "
                + "patient_id = :patientId, "
                + "doctor_id = :doctorId, "
                + "appointment_id = :appointmentId, "
                + "patient_first_name = :patientFirstName, "
                + "patient_last_name = :patientLastName, "
                + "patient_document_type = :patientDocumentType, "
                + "patient_document_number = :patientDocumentNumber, "
                + "patient_gender = :patientGender, "
                + "patient_birth_date = :patientBirthDate, "
                + "patient_district = :patientDistrict, "
                + "patient_province = :patientProvince, "
                + "patient_department = :patientDepartment, "
                + "patient_marital_status = :patientMaritalStatus, "
                + "patient_phone = :patientPhone, "
                + "doctor_first_name = :doctorFirstName, "
                + "doctor_last_name = :doctorLastName, "
                + "doctor_mothers_last_name = :doctorMothersLastName, "
                + "doctor_document_type = :doctorDocumentType, "
                + "doctor_document_number = :doctorDocumentNumber, "
                + "doctor_phone = :doctorPhone, "
                + "specialty_id = :specialtyId, "
                + "specialty_name = :specialtyName, "
                + "visit_date = :visitDate, "
                + "birth_place = :birthPlace, "
                + "occupation = :occupation, "
                + "diagnosis = :diagnosis, "
                + "cie10_code = :cie10Code, "
                + "anamnesis = :anamnesis, "
                + "background = :background, "
                + "medical_evaluation = :medicalEvaluation, "
                + "observations = :observations, "
                + "status = :status, "
                + "updated_at = :updatedAt "
                + "WHERE id = :id "
                + "RETURNING *";

        DatabaseClient.GenericExecuteSpec spec = db.sql(sql)
                .bind("id", mh.getId());

        spec = bindNullableString(spec, "patientId", mh.getPatientId());
        spec = bindNullableUuid(spec, "doctorId", mh.getDoctorId());
        spec = bindNullableString(spec, "appointmentId", mh.getAppointmentId());

        spec = bindNullableString(spec, "patientFirstName", mh.getPatientFirstName());
        spec = bindNullableString(spec, "patientLastName", mh.getPatientLastName());
        spec = bindNullableString(spec, "patientDocumentType", mh.getPatientDocumentType());
        spec = bindNullableString(spec, "patientDocumentNumber", mh.getPatientDocumentNumber());
        spec = bindNullableString(spec, "patientGender", mh.getPatientGender());
        spec = bindNullableDate(spec, "patientBirthDate", mh.getPatientBirthDate());
        spec = bindNullableString(spec, "patientDistrict", mh.getPatientDistrict());
        spec = bindNullableString(spec, "patientProvince", mh.getPatientProvince());
        spec = bindNullableString(spec, "patientDepartment", mh.getPatientDepartment());
        spec = bindNullableString(spec, "patientMaritalStatus", mh.getPatientMaritalStatus());
        spec = bindNullableString(spec, "patientPhone", mh.getPatientPhone());

        spec = bindNullableString(spec, "doctorFirstName", mh.getDoctorFirstName());
        spec = bindNullableString(spec, "doctorLastName", mh.getDoctorLastName());
        spec = bindNullableString(spec, "doctorMothersLastName", mh.getDoctorMothersLastName());
        spec = bindNullableString(spec, "doctorDocumentType", mh.getDoctorDocumentType());
        spec = bindNullableString(spec, "doctorDocumentNumber", mh.getDoctorDocumentNumber());
        spec = bindNullableString(spec, "doctorPhone", mh.getDoctorPhone());
        spec = bindNullableString(spec, "specialtyId", mh.getSpecialtyId());
        spec = bindNullableString(spec, "specialtyName", mh.getSpecialtyName());

        spec = bindNullableDateTime(spec, "visitDate", mh.getVisitDate());
        spec = bindNullableString(spec, "birthPlace", mh.getBirthPlace());
        spec = bindNullableString(spec, "occupation", mh.getOccupation());
        spec = bindNullableString(spec, "diagnosis", mh.getDiagnosis());
        spec = bindNullableString(spec, "cie10Code", mh.getCie10Code());
        spec = bindNullableString(spec, "anamnesis", mh.getAnamnesis());
        spec = bindNullableString(spec, "background", mh.getBackground());
        spec = bindNullableString(spec, "medicalEvaluation", mh.getMedicalEvaluation());
        spec = bindNullableString(spec, "observations", mh.getObservations());

        spec = spec.bind("status", mh.getStatus() != null ? mh.getStatus() : "ACTIVE")
                .bind("updatedAt", now);

        return spec
                .map((row, rowMetadata) -> rowToDocument(row))
                .one()
                .map(this::toModel)
                .doOnSuccess(saved -> log.info("Historia clinica actualizada: id={}, historyNumber={}",
                        saved.getId(), saved.getHistoryNumber()))
                .doOnError(err -> log.error("Error al actualizar historia clinica: {}", err.getMessage()));
    }

    @Override
    public Mono<MedicalHistory> findById(UUID id) {
        return r2dbcRepository.findById(id)
                .map(this::toModel);
    }

    @Override
    public Flux<MedicalHistory> findAll() {
        return r2dbcRepository.findAll()
                .map(this::toModel);
    }

    @Override
    public Flux<MedicalHistory> findByStatus(String status) {
        return r2dbcRepository.findByStatus(status)
                .map(this::toModel);
    }

    @Override
    public Flux<MedicalHistory> findByPatientId(String patientId) {
        return r2dbcRepository.findByPatientId(patientId)
                .map(this::toModel);
    }

    @Override
    public Flux<MedicalHistory> findByDoctorId(UUID doctorId) {
        return r2dbcRepository.findByDoctorId(doctorId)
                .map(this::toModel);
    }

    @Override
    public Flux<MedicalHistory> findByAppointmentId(String appointmentId) {
        return r2dbcRepository.findByAppointmentId(appointmentId)
                .map(this::toModel);
    }

    @Override
    public Flux<MedicalHistory> findByVisitDateBetween(LocalDate start, LocalDate end) {
        // visit_date ahora es timestamp (fecha + hora exacta). El filtro por
        // fecha (LocalDate, sin hora) que llega de la búsqueda se traduce al
        // rango [00:00:00 del día start, 23:59:59.999999999 del día end].
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime   = end.atTime(23, 59, 59, 999_999_999);
        String sql = "SELECT * FROM medical_histories "
                + "WHERE visit_date BETWEEN :start AND :end "
                + "ORDER BY visit_date DESC";
        return db.sql(sql)
                .bind("start", startDateTime)
                .bind("end", endDateTime)
                .map((row, rowMetadata) -> rowToDocument(row))
                .all()
                .map(this::toModel);
    }

    @Override
    public Flux<MedicalHistory> findByPatientIdAndStatus(String patientId, String status) {
        return r2dbcRepository.findByPatientIdAndStatus(patientId, status)
                .map(this::toModel);
    }

    @Override
    public Mono<Boolean> existsById(UUID id) {
        return r2dbcRepository.existsById(id);
    }

    @Override
    public Mono<Long> nextHistoryNumberSequence() {
        return db.sql("SELECT nextval('medical_history_number_seq') AS next_value")
                .map((row, rowMetadata) -> row.get("next_value", Long.class))
                .one();
    }

    // ════════════════════════════════════════════════════════════════════════
    // CONVERSIONES
    // ════════════════════════════════════════════════════════════════════════

    private MedicalHistoryDocument rowToDocument(io.r2dbc.spi.Row row) {
        return MedicalHistoryDocument.builder()
                .id(row.get("id", UUID.class))
                .historyNumber(row.get("history_number", String.class))
                .patientId(row.get("patient_id", String.class))
                .doctorId(row.get("doctor_id", UUID.class))
                .appointmentId(row.get("appointment_id", String.class))
                .patientFirstName(row.get("patient_first_name", String.class))
                .patientLastName(row.get("patient_last_name", String.class))
                .patientDocumentType(row.get("patient_document_type", String.class))
                .patientDocumentNumber(row.get("patient_document_number", String.class))
                .patientGender(row.get("patient_gender", String.class))
                .patientBirthDate(row.get("patient_birth_date", LocalDate.class))
                .patientDistrict(row.get("patient_district", String.class))
                .patientProvince(row.get("patient_province", String.class))
                .patientDepartment(row.get("patient_department", String.class))
                .patientMaritalStatus(row.get("patient_marital_status", String.class))
                .patientPhone(row.get("patient_phone", String.class))
                .doctorFirstName(row.get("doctor_first_name", String.class))
                .doctorLastName(row.get("doctor_last_name", String.class))
                .doctorMothersLastName(row.get("doctor_mothers_last_name", String.class))
                .doctorDocumentType(row.get("doctor_document_type", String.class))
                .doctorDocumentNumber(row.get("doctor_document_number", String.class))
                .doctorPhone(row.get("doctor_phone", String.class))
                .specialtyId(row.get("specialty_id", String.class))
                .specialtyName(row.get("specialty_name", String.class))
                .visitDate(row.get("visit_date", LocalDateTime.class))
                .birthPlace(row.get("birth_place", String.class))
                .occupation(row.get("occupation", String.class))
                .diagnosis(row.get("diagnosis", String.class))
                .cie10Code(row.get("cie10_code", String.class))
                .anamnesis(row.get("anamnesis", String.class))
                .background(row.get("background", String.class))
                .medicalEvaluation(row.get("medical_evaluation", String.class))
                .observations(row.get("observations", String.class))
                .status(row.get("status", String.class))
                .createdAt(row.get("created_at", LocalDateTime.class))
                .updatedAt(row.get("updated_at", LocalDateTime.class))
                .build();
    }

    private MedicalHistory toModel(MedicalHistoryDocument doc) {
        return MedicalHistory.builder()
                .id(doc.getId())
                .historyNumber(doc.getHistoryNumber())
                .patientId(doc.getPatientId())
                .doctorId(doc.getDoctorId())
                .appointmentId(nullIfEmpty(doc.getAppointmentId()))
                .patientFirstName(nullIfEmpty(doc.getPatientFirstName()))
                .patientLastName(nullIfEmpty(doc.getPatientLastName()))
                .patientDocumentType(nullIfEmpty(doc.getPatientDocumentType()))
                .patientDocumentNumber(nullIfEmpty(doc.getPatientDocumentNumber()))
                .patientGender(nullIfEmpty(doc.getPatientGender()))
                .patientBirthDate(doc.getPatientBirthDate())
                .patientDistrict(nullIfEmpty(doc.getPatientDistrict()))
                .patientProvince(nullIfEmpty(doc.getPatientProvince()))
                .patientDepartment(nullIfEmpty(doc.getPatientDepartment()))
                .patientMaritalStatus(nullIfEmpty(doc.getPatientMaritalStatus()))
                .patientPhone(nullIfEmpty(doc.getPatientPhone()))
                .doctorFirstName(nullIfEmpty(doc.getDoctorFirstName()))
                .doctorLastName(nullIfEmpty(doc.getDoctorLastName()))
                .doctorMothersLastName(nullIfEmpty(doc.getDoctorMothersLastName()))
                .doctorDocumentType(nullIfEmpty(doc.getDoctorDocumentType()))
                .doctorDocumentNumber(nullIfEmpty(doc.getDoctorDocumentNumber()))
                .doctorPhone(nullIfEmpty(doc.getDoctorPhone()))
                .specialtyId(nullIfEmpty(doc.getSpecialtyId()))
                .specialtyName(nullIfEmpty(doc.getSpecialtyName()))
                .visitDate(doc.getVisitDate())
                .birthPlace(nullIfEmpty(doc.getBirthPlace()))
                .occupation(nullIfEmpty(doc.getOccupation()))
                .diagnosis(nullIfEmpty(doc.getDiagnosis()))
                .cie10Code(nullIfEmpty(doc.getCie10Code()))
                .anamnesis(nullIfEmpty(doc.getAnamnesis()))
                .background(nullIfEmpty(doc.getBackground()))
                .medicalEvaluation(nullIfEmpty(doc.getMedicalEvaluation()))
                .observations(nullIfEmpty(doc.getObservations()))
                .status(doc.getStatus())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    private String nullIfEmpty(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }
}
