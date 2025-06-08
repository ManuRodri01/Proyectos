package api.DTO;

import api.Entity.EstadoSolicitud;

import java.time.LocalDate;

public record SolicitudCartaDTO(
        String idSolicitud,
        String nombreCarta,
        String nombreJuego,
        String idJuego,
        EstadoSolicitud estado,
        LocalDate fechaSolicitud,
        LocalDate fechaCierre
) {
}
