package grupo1.tpTACS_API.DTO;

import grupo1.tpTACS_API.Entity.EstadoConservacion;

import java.time.LocalDateTime;
import java.util.List;

public record RespuetaPublicacionDTO(
        String titulo,
        String nombreCarta,
        String nombreJuego,
        String descripcion,
        List<String> intereses,
        List<String> imagenes,
        Float precio,
        EstadoConservacion estado,
        LocalDateTime fechaPublicacion,
        String nombrePublicador,
        Boolean esPublicador,
        Integer id
) {
}
