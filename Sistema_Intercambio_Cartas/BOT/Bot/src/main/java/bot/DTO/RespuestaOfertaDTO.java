package bot.DTO;

import java.time.LocalDate;
import java.util.List;

public record RespuestaOfertaDTO(
        String tituloPublicacion,
        String nombreOfertante,
        String nombrePublicador,
        String id,
        String estadoOferta,
        LocalDate fecha,
        Float valor,
        List<String> cartas,
        String imagen,
        Boolean esPublicador
) {
}
