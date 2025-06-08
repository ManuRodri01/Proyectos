package api.DTO;

import java.util.List;

public record CreacionOfertaDTO (
  String publicacionId,
  List<String> cartas,
  Float valor
){}
