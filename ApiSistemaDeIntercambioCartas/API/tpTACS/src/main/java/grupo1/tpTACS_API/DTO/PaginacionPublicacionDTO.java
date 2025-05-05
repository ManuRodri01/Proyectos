package grupo1.tpTACS_API.DTO;

import grupo1.tpTACS_API.Entity.Publicacion;

import java.util.List;

public record PaginacionPublicacionDTO(
        List<PublicacionReducidaDTO> publicaciones,
        int pagAnterior,
        int pagSiguiente
) {
}
