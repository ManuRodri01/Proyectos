package grupo1.tpTACS_API.DTO;

import grupo1.tpTACS_API.Entity.EstadoConservacion;

public record PublicacionReducidaDTO(
        String titulo,
        String imagen,
        EstadoConservacion estado,
        String tituloCarta,
        String refAPublicacion,
        int id
) {
}
