package api.DTO;



import api.Entity.EstadoConservacion;

import java.time.LocalDateTime;

public record PublicacionesDePersonaDTO(
        String titulo,
        String imagen,
        EstadoConservacion estado,
        String tituloCarta,
        LocalDateTime fechaPublicacion,
        LocalDateTime fechaCierre,
        String refAPublicacion,
        String id
) {
}
