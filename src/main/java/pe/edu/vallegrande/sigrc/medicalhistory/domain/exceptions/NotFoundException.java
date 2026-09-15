package pe.edu.vallegrande.sigrc.medicalhistory.domain.exceptions;

public class NotFoundException extends DomainException {
    public NotFoundException(String message) {
        super(message);
    }
}
