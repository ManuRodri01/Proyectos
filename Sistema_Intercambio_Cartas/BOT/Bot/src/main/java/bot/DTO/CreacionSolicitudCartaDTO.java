package bot.DTO;

import jakarta.validation.constraints.NotBlank;

public record CreacionSolicitudCartaDTO(
        String nombreCarta,
        String idJuego
) {
}
