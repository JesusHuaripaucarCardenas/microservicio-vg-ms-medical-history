package pe.edu.vallegrande.sigrc.medicalhistory.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateMedicalHistoryRequest {

    private String patientId;

    @NotBlank(message = "El nombre del paciente es requerido")
    private String patientFirstName;

    @NotBlank(message = "El apellido del paciente es requerido")
    private String patientLastName;

    @Pattern(regexp = "DNI|CE|CIE|PTP|CPP|PASSPORT", message = "Tipo de documento inválido")
    private String patientDocumentType;

    private String patientDocumentNumber;

    @Pattern(regexp = "MASCULINO|FEMENINO", message = "El sexo debe ser MASCULINO o FEMENINO")
    private String patientGender;

    private LocalDate patientBirthDate;

    private String patientDistrict;
    private String patientProvince;
    private String patientDepartment;
    private String patientMaritalStatus;
    private String patientPhone;

    @Size(max = 200, message = "El lugar de nacimiento no puede exceder 200 caracteres")
    private String birthPlace;

    @Size(max = 150)
    private String occupation;

    @NotNull(message = "El ID del médico es requerido")
    private UUID doctorId;

    @NotBlank(message = "El apellido del médico es requerido")
    private String doctorLastName;
    private String doctorFirstName;
    private String doctorMothersLastName;
    private String doctorDocumentType;
    private String doctorDocumentNumber;
    private String doctorPhone;

    @NotBlank(message = "El servicio (especialidad) es requerido")
    private String specialtyId;
    private String specialtyName;

    private String appointmentId;

    // (cambio) La fecha/hora de la consulta ya NO la envía el cliente: se
    // asigna automáticamente en el servidor al momento de crear (ver
    // CreateMedicalHistoryUseCaseImpl), con fecha y hora exactas.

    private String diagnosis;
    @Size(max = 20)
    private String cie10Code;
    private String anamnesis;
    private String background;
    private String medicalEvaluation;
    private String observations;
}
