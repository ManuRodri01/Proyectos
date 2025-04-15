package manuRodri.intercambioCartas.DTO;

import manuRodri.intercambioCartas.Entity.estadoConservacion;
import java.util.List;

public record CreacionPublicacionDTO(
    Integer cartaId,
    String descripcion,
    List<Integer> intereses,
    List<String> imagenes,
    Float precio,
    estadoConservacion estadoConservacion
){}