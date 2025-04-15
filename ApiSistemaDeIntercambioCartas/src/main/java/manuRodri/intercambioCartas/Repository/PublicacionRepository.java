package manuRodri.intercambioCartas.Repository;


import manuRodri.intercambioCartas.DTO.FiltroPublicacionesDTO;
import manuRodri.intercambioCartas.Entity.Publicacion;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class PublicacionRepository {
  
    private Map<Integer, Publicacion> tablaPublicaciones = new HashMap<>();

    public PublicacionRepository() {

    }

    private AtomicInteger idPublicacion = new AtomicInteger(0);
    public boolean existePublicacion(int id) {
        return tablaPublicaciones.containsKey(id);
    }

    public Publicacion getPublicacion(int publicacionId){
        return tablaPublicaciones.get(publicacionId);
    }

    public List<Publicacion> getAllPublicaciones(){

        return tablaPublicaciones.values().stream().toList();
    }

    public void clear(){
        tablaPublicaciones.clear();
    }

    public List<Publicacion> getPublicacionesByJuego(int juegoId){
        List<Publicacion> publicaciones = new ArrayList<>();
        for (Publicacion publicacion : tablaPublicaciones.values()) {
            if(publicacion.getCarta().getJuego().getId() == juegoId) {
                publicaciones.add(publicacion);
            }
        }
        return publicaciones;
    }

    public List<Publicacion> getPublicacionesByCarta(int cartaId){
        List<Publicacion> publicaciones = new ArrayList<>();
        for (Publicacion publicacion : tablaPublicaciones.values()) {
            if(publicacion.getCarta().getId() == cartaId) {
                publicaciones.add(publicacion);
            }
        }
        return publicaciones;
    }

    public Publicacion guardarPublicacion(Publicacion publicacion){
        publicacion.setId(idPublicacion.getAndIncrement());
        tablaPublicaciones.put(publicacion.getId(), publicacion);
        return publicacion;
    }

    public Publicacion cerrarPublicacion(int id){
        Publicacion publicacion = tablaPublicaciones.get(id);
        publicacion.setFechaCierre(LocalDateTime.now());
        tablaPublicaciones.put(id, publicacion);
        return publicacion;
    }

    public List<Publicacion> getPublicacionesByPersona(String nombre) {
        List<Publicacion> publicaciones = new ArrayList<>();

        return publicaciones;
    }


    /**
     * Devuelve las publicaciones aplicando los distintos filtros.
     * Si no se desea usar un filtro en específico, en el DTO se debe marcar ese filtro con null
     * @param filtro un FiltroPublicacionesDTO que contiene todos los filtros a aplicar
     * @return List\<Publicacion\> con todas las publicaciones que cumplen los filtros
     */
    public List<Publicacion> getPublicaciones(FiltroPublicacionesDTO filtro) {
        return getAllPublicaciones().stream()
                .filter(publicacion -> filtro.cartaId() == null || publicacion.getCarta().getId() == filtro.cartaId())
                .filter(publicacion -> filtro.juegoId() == null || publicacion.getCarta().getJuego().getId() == filtro.juegoId())
                .filter(publicacion -> filtro.nombrePublicacion() == null || publicacion.getDescripcion().contains(filtro.nombrePublicacion()))
                .filter(publicacion -> filtro.estadoConservacion() == null || publicacion.getEstadoConservacion().equals(filtro.estadoConservacion()))
                .filter(publicacion -> filtro.desdeFecha() == null || publicacion.getFechaPublicacion().isAfter(filtro.desdeFecha()))
                .filter(publicacion -> filtro.hastaFecha() == null || publicacion.getFechaPublicacion().isBefore(filtro.hastaFecha()))
                .filter(publicacion -> filtro.desdePrecio() == null || publicacion.getPrecio() >= filtro.desdePrecio())
                .filter(publicacion -> filtro.hastaPrecio() == null || publicacion.getPrecio() <= filtro.hastaPrecio())
                .filter(publicacion -> filtro.intereses() == null || filtro.intereses().stream().anyMatch(carta -> publicacion.getIntereses().contains(carta)))
                .toList();
    }
}

