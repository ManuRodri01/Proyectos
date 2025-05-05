package grupo1.tpTACS_API.Repository;



import grupo1.tpTACS_API.DTO.FiltroPublicacionesDTO;
import grupo1.tpTACS_API.DTO.PaginacionPublicacionDTO;
import grupo1.tpTACS_API.DTO.PublicacionReducidaDTO;
import grupo1.tpTACS_API.Entity.Persona;
import grupo1.tpTACS_API.Entity.Publicacion;
import grupo1.tpTACS_API.Exception.PaginaInexistenteException;
import grupo1.tpTACS_API.Service.PublicacionesMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Repository
public class PublicacionRepository {
  
    private Map<Integer, Publicacion> tablaPublicaciones = new HashMap<>();
    private final PublicacionesMapper publicacionesMapper;

    private int tamanioPagina = 10;

    public PublicacionRepository(PublicacionesMapper publicacionesMapper) {

        this.publicacionesMapper = publicacionesMapper;
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

    public void borrarPublicacion(int publicacionId, Persona persona) {
        existePublicacion(publicacionId);
        Publicacion publicacion = tablaPublicaciones.get(publicacionId);
        if(publicacion.getPublicador().getNombre().equals(persona.getNombre())) {
            tablaPublicaciones.remove(publicacionId);
        }
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
        return publicacion;
    }

    public List<Publicacion> getPublicacionesByPersona(String nombre) {
        List<Publicacion> publicaciones = new ArrayList<>();
        for (Publicacion publicacion : tablaPublicaciones.values()) {
            if(publicacion.getPublicador().getNombre().equals(nombre)) {
                publicaciones.add(publicacion);
            }
        }
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
                .filter(publicacion -> filtro.nombrePublicacion() == null || publicacion.getTitulo().toLowerCase().contains(filtro.nombrePublicacion().toLowerCase()))
                .filter(publicacion -> filtro.estadoConservacion() == null || publicacion.getEstadoConservacion().equals(filtro.estadoConservacion()))
                .filter(publicacion -> filtro.desdeFecha() == null || publicacion.getFechaPublicacion().isAfter(filtro.desdeFecha()))
                .filter(publicacion -> filtro.hastaFecha() == null || publicacion.getFechaPublicacion().isBefore(filtro.hastaFecha()))
                .filter(publicacion -> filtro.desdePrecio() == null || publicacion.getPrecio() >= filtro.desdePrecio())
                .filter(publicacion -> filtro.hastaPrecio() == null || publicacion.getPrecio() <= filtro.hastaPrecio())
                .filter(publicacion -> filtro.intereses() == null || filtro.intereses().stream().anyMatch(cartaId -> publicacion.getIntereses().stream().anyMatch(cartaIntereses -> cartaIntereses.getId() == cartaId)))
                .toList();
    }

    public PaginacionPublicacionDTO getPublicaciones(FiltroPublicacionesDTO filtro, int pagina) {
        List<Publicacion> publicaciones = getPublicaciones(filtro);

        if(publicaciones.isEmpty()) {
            return new PaginacionPublicacionDTO(Collections.emptyList(), -1, -1);
        }
        if(pagina < 1 || Math.ceil(publicaciones.size() / (double) tamanioPagina) < pagina ) {
            throw new PaginaInexistenteException(pagina);
        }

        int desde = (pagina - 1) * tamanioPagina ;
        int hasta = Math.min(desde + tamanioPagina, publicaciones.size());

        int pagAnterior = ((pagina - 1) < 1) ? -1 : (pagina - 1);
        int pagSiguinete = (Math.ceil(publicaciones.size() / (double) tamanioPagina) < pagina + 1 ) ? -1 : (pagina + 1);

        List<Publicacion> publicacionesPaginadas = publicaciones.subList(desde,hasta);

        return new PaginacionPublicacionDTO(publicacionesPaginadas.stream().map(publicacionesMapper::toPublicacionReducidaDTO).toList(), pagAnterior, pagSiguinete);

    }
}

