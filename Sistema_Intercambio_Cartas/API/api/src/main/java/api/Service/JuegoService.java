package api.Service;


import api.DTO.CreacionJuegoDTO;
import api.Entity.Juego;
import api.Exception.JuegoNoEncontradoException;
import api.Repository.JuegoRepository;
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

    public Juego getJuego(String id) {
        Juego juego = juegoRepository.findOneById(id);
        if (juego == null) {
            throw new JuegoNoEncontradoException(id);
        }
        return juego;
    }

    public boolean existeJuegoPorNombre(String nombreJuego) {
        return juegoRepository.existeJuegoNombre(nombreJuego);
    }

    public boolean existeJuegoPorIdJuego(String idJuego) {
        return juegoRepository.existeJuego(idJuego);
    }

    public List<Juego> borrarJuego(String id) {
        return juegoRepository.borrarJuego(id);
    }

    public Juego guardarJuego(CreacionJuegoDTO juego) {
        return juegoRepository.guardarJuego(juego);
    }
}
