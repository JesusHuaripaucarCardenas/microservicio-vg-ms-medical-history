package pe.edu.vallegrande.sigrc.medicalhistory.domain.exceptions;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
