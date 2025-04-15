package manuRodri.intercambioCartas.Repository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import manuRodri.intercambioCartas.DTO.CreacionJuegoDTO;
import manuRodri.intercambioCartas.Entity.Juego;
import org.springframework.stereotype.Repository;

@Repository
public class JuegoRepository {

    private Map<Integer, Juego> tablaJuegos = new HashMap<>();
    private AtomicInteger idJuego = new AtomicInteger(0);

    public JuegoRepository(){

    }

    public List<Juego> getTodos(){
        return new ArrayList<>(tablaJuegos.values());
    }

    public Juego findOneById(int id){
        return tablaJuegos.get(id);
    }

    public Juego guardarJuego(CreacionJuegoDTO juego){
        Juego juegoGuardado = new Juego();
        juegoGuardado.setId(idJuego.getAndIncrement());
        juegoGuardado.setNombre(juego.nombre());
        tablaJuegos.put(juegoGuardado.getId(), juegoGuardado);
        return juegoGuardado;
    }

    public List<Juego> borrarJuego(int id){
        tablaJuegos.remove(id);
        return new ArrayList<Juego>(tablaJuegos.values());
    }

}
