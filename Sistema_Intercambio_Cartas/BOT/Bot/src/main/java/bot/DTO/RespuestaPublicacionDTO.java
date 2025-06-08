package bot.DTO;

import java.time.LocalDateTime;
import java.util.List;

public record RespuestaPublicacionDTO(
        String titulo,
        String nombreCarta,
        String nombreJuego,
        String descripcion,
        List<String> intereses,
        List<String> imagenes,
        Float precio,
        String estado,
        LocalDateTime fechaPublicacion,
        String nombrePublicador,
        Boolean esPublicador,
        String id
) {
}
