package api.DTO;

import api.Entity.EstadoOferta;

import java.time.LocalDate;

public record OfertaPersonaDTO(
        String nombreCarta,
        EstadoOferta estado,
        String tituloPublicacion,
        String nombreOfertante,
        LocalDate fecha,
        String imagen,
        String refAOferta,
        String id
) {
}
