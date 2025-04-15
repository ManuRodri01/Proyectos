package manuRodri.intercambioCartas.DTO;

import java.util.List;

public record CreacionOfertaDTO (
  Integer publicacionId,
  List<Integer> cartas,
  Float valor
){}
