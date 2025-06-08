package api.DTO;

import api.Entity.EstadoConservacion;

public record PublicacionReducidaDTO(
        String titulo,
        String imagen,
        EstadoConservacion estado,
        String tituloCarta,
        String refAPublicacion,
        String id
) {
}
