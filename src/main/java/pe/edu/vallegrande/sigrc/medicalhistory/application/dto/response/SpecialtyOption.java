package pe.edu.vallegrande.sigrc.medicalhistory.application.dto.response;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SpecialtyOption {
    private String id;
    private String name;
}
