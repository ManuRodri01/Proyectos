package bot.DTO;

import java.util.List;

public record CreacionPublicacionDTO(
        String cartaId,
        String titulo,
        String descripcion,
        List<String>intereses,
        List<String> imagenes,
        Float precio,
        String estadoConservacion
) {
}
