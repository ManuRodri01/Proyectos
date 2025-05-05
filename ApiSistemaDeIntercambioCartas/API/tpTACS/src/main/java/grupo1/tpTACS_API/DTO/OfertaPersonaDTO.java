package grupo1.tpTACS_API.DTO;

import grupo1.tpTACS_API.Entity.EstadoOferta;

import java.time.LocalDate;

public record OfertaPersonaDTO(
        String nombreCarta,
        EstadoOferta estado,
        String tituloPublicacion,
        String nombreOfertante,
        LocalDate fecha,
        String imagen,
        String refAOferta,
        Integer id
) {
}
