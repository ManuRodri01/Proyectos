package api.DTO;

import api.Entity.EstadoSolicitud;

import java.time.LocalDate;

public record SolicitudJuegoDTO(
        String idSolicitud,
        String nombreJuego,
        EstadoSolicitud estado,
        LocalDate fechaSolicitud,
        LocalDate fechaCierre
) {
}
