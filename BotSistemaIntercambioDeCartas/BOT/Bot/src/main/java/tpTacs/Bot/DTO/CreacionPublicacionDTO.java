package tpTacs.Bot.DTO;

import java.util.List;

public record CreacionPublicacionDTO(
        Integer cartaId,
        String titulo,
        String descripcion,
        List<Integer>intereses,
        List<String> imagenes,
        Float precio,
        String estadoConservacion
) {
}
