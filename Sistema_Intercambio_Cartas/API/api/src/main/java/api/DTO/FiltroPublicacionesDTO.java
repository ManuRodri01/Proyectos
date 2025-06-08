package api.DTO;


import api.Entity.EstadoConservacion;

import java.time.LocalDateTime;
import java.util.List;


public record FiltroPublicacionesDTO(
        String cartaId,
        String juegoId,
        String nombrePublicacion,
        EstadoConservacion estadoConservacion,
        LocalDateTime desdeFecha,
        LocalDateTime hastaFecha,
        Float desdePrecio,
        Float hastaPrecio,
        List<String> intereses
){
}
