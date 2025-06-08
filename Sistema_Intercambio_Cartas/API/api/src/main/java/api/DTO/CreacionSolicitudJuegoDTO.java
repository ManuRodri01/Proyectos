package api.DTO;

import jakarta.validation.constraints.NotBlank;

public record CreacionSolicitudJuegoDTO(
        @NotBlank
        String nombreJuego
) {
}
