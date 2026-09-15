package pe.edu.vallegrande.sigrc.medicalhistory.application.dto.response;

import lombok.*;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DoctorLookupResponse {
    private UUID doctorId;
    private String firstName;
    private String lastName;
    private String motherLastName;
    private String documentType;
    private String documentNumber;
    private String phone;
}
