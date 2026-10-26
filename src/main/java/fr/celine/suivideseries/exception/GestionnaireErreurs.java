package fr.celine.suivideseries.exception;

import fr.celine.suivideseries.dto.ErreurDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GestionnaireErreurs {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErreurDTO> gererBusinessException(BusinessException ex) {
        ErreurDTO erreur = new ErreurDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurDTO> gererValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" "));

        ErreurDTO erreur = new ErreurDTO(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }
}
