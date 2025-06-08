package bot.DTO;

import java.time.LocalDate;

public record SolicitudJuegoDTO(
        String idSolicitud,
        String nombreJuego,
        String estado,
        LocalDate fechaSolicitud,
        LocalDate fechaCierre
) {
}
