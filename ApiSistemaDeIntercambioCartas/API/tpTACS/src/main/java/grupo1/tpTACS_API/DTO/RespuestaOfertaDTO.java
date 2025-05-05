package grupo1.tpTACS_API.DTO;

import grupo1.tpTACS_API.Entity.EstadoOferta;

import java.time.LocalDate;
import java.util.List;

public record RespuestaOfertaDTO(
        String tituloPublicacion,
        String nombreOfertante,
        String nombrePublicador,
        Integer id,
        EstadoOferta estadoOferta,
        LocalDate fecha,
        Float valor,
        List<String> cartas,
        String imagen,
        Boolean esPublicador
) {
}
