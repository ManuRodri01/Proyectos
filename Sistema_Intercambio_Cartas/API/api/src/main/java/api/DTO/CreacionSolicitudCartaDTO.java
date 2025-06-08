package api.DTO;

import jakarta.validation.constraints.NotBlank;

public record CreacionSolicitudCartaDTO(
        @NotBlank
        String nombreCarta,
        @NotBlank
        String idJuego
) {
}
