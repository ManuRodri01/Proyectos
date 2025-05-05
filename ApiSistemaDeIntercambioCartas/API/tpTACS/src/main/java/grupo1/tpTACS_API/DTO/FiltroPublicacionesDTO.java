package grupo1.tpTACS_API.DTO;


import grupo1.tpTACS_API.Entity.EstadoConservacion;

import java.time.LocalDateTime;
import java.util.List;


public record FiltroPublicacionesDTO(
        Integer cartaId,
        Integer juegoId,
        String nombrePublicacion,
        EstadoConservacion estadoConservacion,
        LocalDateTime desdeFecha,
        LocalDateTime hastaFecha,
        Float desdePrecio,
        Float hastaPrecio,
        List<Integer> intereses
){
}
