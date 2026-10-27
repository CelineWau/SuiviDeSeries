package fr.celine.suivideseries.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ModificationEmailDTO {
    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email doit avoir une forme valide.")
    private final String email;

    public ModificationEmailDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
