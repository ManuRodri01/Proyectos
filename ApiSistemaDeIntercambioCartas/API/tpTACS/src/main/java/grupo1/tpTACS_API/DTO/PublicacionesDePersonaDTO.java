package grupo1.tpTACS_API.DTO;



import grupo1.tpTACS_API.Entity.EstadoConservacion;

import java.time.LocalDateTime;

public record PublicacionesDePersonaDTO(
        String titulo,
        String imagen,
        EstadoConservacion estado,
        String tituloCarta,
        LocalDateTime fechaPublicacion,
        LocalDateTime fechaCierre,
        String refAPublicacion,
        int id
) {
}
