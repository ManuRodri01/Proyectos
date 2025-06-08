package bot.DTO;

import java.time.LocalDate;

public record SolicitudCartaDTO(
        String idSolicitud,
        String nombreCarta,
        String nombreJuego,
        String idJuego,
        String estado,
        LocalDate fechaSolicitud,
        LocalDate fechaCierre
) {
}
