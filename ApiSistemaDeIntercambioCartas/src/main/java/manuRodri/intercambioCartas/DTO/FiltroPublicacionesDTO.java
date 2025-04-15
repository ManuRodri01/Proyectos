package manuRodri.intercambioCartas.DTO;

import manuRodri.intercambioCartas.Entity.Carta;
import manuRodri.intercambioCartas.Entity.estadoConservacion;

import java.time.LocalDateTime;
import java.util.List;


public record FiltroPublicacionesDTO(
        Integer cartaId,
        Integer juegoId,
        String nombrePublicacion,
        estadoConservacion estadoConservacion,
        LocalDateTime desdeFecha,
        LocalDateTime hastaFecha,
        Float desdePrecio,
        Float hastaPrecio,
        List<Carta> intereses
){
}
