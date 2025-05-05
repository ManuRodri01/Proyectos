package grupo1.tpTACS_API.DTO;

import java.util.List;

public record CreacionOfertaDTO (
  Integer publicacionId,
  List<Integer> cartas,
  Float valor
){}
