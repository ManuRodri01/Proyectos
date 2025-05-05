package grupo1.tpTACS_API.DTO;


import grupo1.tpTACS_API.Entity.EstadoConservacion;

import java.util.List;

public record CreacionPublicacionDTO(
    Integer cartaId,
    String titulo,
    String descripcion,
    List<Integer> intereses,
    List<String> imagenes,
    Float precio,
    EstadoConservacion estadoConservacion
){}