package manuRodri.intercambioCartas.Service;

import manuRodri.intercambioCartas.DTO.CreacionJuegoDTO;
import manuRodri.intercambioCartas.Entity.Juego;
import manuRodri.intercambioCartas.Repository.JuegoRepository;
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
        return juegoRepository.findOneById(id);
    }

    public List<Juego> borrarJuego(int id) {
        return juegoRepository.borrarJuego(id);
    }

    public Juego guardarJuego(CreacionJuegoDTO juego) {
        return juegoRepository.guardarJuego(juego);
    }
}
