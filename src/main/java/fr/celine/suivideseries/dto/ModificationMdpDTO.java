package fr.celine.suivideseries.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ModificationMdpDTO {

    @NotBlank(message = "L'ancien mot de passe est obligatoire.")
    private final String ancienMdp;
    @NotBlank(message = "Le nouveau mot de passe est obligatoire.")
    @Size(min = 8, max = 72, message = "Le mot de passe doit faire entre 8 et 72 caractères.")
    private final String nouveauMdp;

    public ModificationMdpDTO(String ancienMdp, String nouveauMdp) {
        this.ancienMdp = ancienMdp;
        this.nouveauMdp = nouveauMdp;
    }

    public String getAncienMdp() {
        return ancienMdp;
    }

    public String getNouveauMdp() {
        return nouveauMdp;
    }
}
