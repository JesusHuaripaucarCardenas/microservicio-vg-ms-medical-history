package pe.edu.vallegrande.sigrc.medicalhistory.domain.models;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistory {

    private UUID id;
    private String historyNumber;

    // ── Referencias externas ──────────────────────────────────────────
    private String patientId;
    private UUID   doctorId;
    private String appointmentId;

    // ── Encabezado de consulta ────────────────────────────────────────
    /** Se asigna automáticamente en el servidor al crear (fecha y hora exactas); no es editable. */
    private LocalDateTime visitDate;
    // (limpieza) se eliminó el campo suelto "service": el servicio mostrado
    // siempre es specialtyName; tener dos campos para lo mismo generaba
    // inconsistencia (nunca se llenaba al crear) y código muerto.

    // ── SNAPSHOT del paciente ─────────────────────────────────────────
    private String    patientFirstName;
    private String    patientLastName;
    private String    patientDocumentType;
    private String    patientDocumentNumber;
    private String    patientGender;
    private LocalDate patientBirthDate;
    private String    patientDistrict;
    private String    patientProvince;
    private String    patientDepartment;
    private String    patientMaritalStatus;
    private String    patientPhone;

    private String birthPlace;
    private String occupation;

    // ── SNAPSHOT del médico ────────────────────────────────────────────
    private String doctorFirstName;
    private String doctorLastName;
    private String doctorMothersLastName;
    private String doctorDocumentType;
    private String doctorDocumentNumber;
    private String doctorPhone;
    private String specialtyId;
    private String specialtyName;

    // ── Diagnóstico ────────────────────────────────────────────────────
    private String diagnosis;
    private String cie10Code;

    // ── Cuerpo clínico ─────────────────────────────────────────────────
    private String anamnesis;
    private String background;
    private String medicalEvaluation;
    private String observations;

    // ── Control ────────────────────────────────────────────────────────
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // (limpieza) se eliminó "patientAge": nunca se persistía ni se leía;
    // la edad siempre se calcula al vuelo en MedicalHistoryMapper#toResponse
    // a partir de patientBirthDate, así que el campo era código muerto.
}
