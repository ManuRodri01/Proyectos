package tpTacs.Bot.DTO;

import java.util.List;

public record PaginacionPublicacionDTO(
        List<PublicacionReducidaDTO> publicaciones,
        int pagAnterior,
        int pagSiguiente
) {
}
