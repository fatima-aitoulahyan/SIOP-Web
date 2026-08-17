package com.example.backend_siop.maintenance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejetDemandeDTO {

    @NotBlank(message = "Le motif de rejet est obligatoire")
    @Size(min = 10, message = "Le motif doit contenir au moins 10 caractères")
    private String motif;
}