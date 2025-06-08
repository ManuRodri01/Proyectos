package api.DTO;

import api.Entity.Publicacion;

import java.util.List;

public record PseudoPaginacionPublicacionDTO(
        List<Publicacion> publicaciones,
        int pagAnterior,
        int pagSiguiente
) {
}
