package fr.celine.suivideseries.exception;

import fr.celine.suivideseries.dto.ErreurDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GestionnaireErreurs {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErreurDTO> gererBusinessException(BusinessException ex) {
        ErreurDTO erreur = new ErreurDTO(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }
}
