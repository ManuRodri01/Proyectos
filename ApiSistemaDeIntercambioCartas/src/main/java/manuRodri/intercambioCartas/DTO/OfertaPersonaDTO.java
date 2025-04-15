package manuRodri.intercambioCartas.DTO;

import manuRodri.intercambioCartas.Entity.estadoOferta;

import java.time.LocalDate;

public record OfertaPersonaDTO(
        String nombreCarta,
        estadoOferta estado,
        String nombreOfertante,
        LocalDate fecha,
        String imagen,
        String refAOferta
) {
}
