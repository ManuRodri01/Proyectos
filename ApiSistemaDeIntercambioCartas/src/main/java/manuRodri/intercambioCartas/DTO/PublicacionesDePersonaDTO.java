package manuRodri.intercambioCartas.DTO;

import manuRodri.intercambioCartas.Entity.estadoConservacion;

import java.time.LocalDateTime;

public record PublicacionesDePersonaDTO(
        String imagen,
        estadoConservacion estado,
        String tituloCarta,
        LocalDateTime fechaPublicacion,
        String refAPublicacion
) {
}
