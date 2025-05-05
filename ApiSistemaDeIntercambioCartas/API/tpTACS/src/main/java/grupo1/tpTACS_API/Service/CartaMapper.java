package grupo1.tpTACS_API.Service;

import grupo1.tpTACS_API.DTO.RespuestaCartaDTO;
import grupo1.tpTACS_API.Entity.Carta;
import org.springframework.stereotype.Service;

@Service
public class CartaMapper {
    public CartaMapper() {}

    public RespuestaCartaDTO toRespuestaCartaDTO(Carta carta) {
        return new RespuestaCartaDTO(carta.getNombre(),carta.getJuego().getNombre(),carta.getJuego().getId(),carta.getId());
    }


}
