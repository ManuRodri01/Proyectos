package grupo1.tpTACS_API.Service;


import grupo1.tpTACS_API.DTO.CreacionJuegoDTO;
import grupo1.tpTACS_API.Entity.Juego;
import grupo1.tpTACS_API.Exception.JuegoNoEncontradoException;
import grupo1.tpTACS_API.Repository.JuegoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JuegoService {

    private final JuegoRepository juegoRepository;

    public JuegoService(JuegoRepository juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    public List<Juego> getJuegos(){
        return juegoRepository.getTodos();
    }

    public Juego getJuego(int id) {
        Juego juego = juegoRepository.findOneById(id);
        if (juego == null) {
            throw new JuegoNoEncontradoException(id);
        }
        return juego;
    }

    public List<Juego> borrarJuego(int id) {
        return juegoRepository.borrarJuego(id);
    }

    public Juego guardarJuego(CreacionJuegoDTO juego) {
        return juegoRepository.guardarJuego(juego);
    }
}
