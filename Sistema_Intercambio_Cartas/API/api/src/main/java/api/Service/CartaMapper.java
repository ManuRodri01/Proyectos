package api.Service;

import api.DTO.RespuestaCartaDTO;
import api.Entity.Carta;
import api.Entity.Juego;
import org.springframework.stereotype.Service;

@Service
public class CartaMapper {
    private final JuegoService juegoService;
    public CartaMapper(JuegoService juegoService) {
        this.juegoService = juegoService;
    }

    public RespuestaCartaDTO toRespuestaCartaDTO(Carta carta) {
        Juego juego = juegoService.getJuego(carta.getJuegoId());
        return new RespuestaCartaDTO(carta.getNombre(),juego.getNombre(),carta.getJuegoId(),carta.getId());
    }


}
