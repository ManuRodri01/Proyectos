package api.Repository;



import api.DTO.FiltroPublicacionesDTO;

import api.DTO.PseudoPaginacionPublicacionDTO;
import api.Entity.Persona;
import api.Entity.Publicacion;
import api.Exception.PaginaInexistenteException;
import api.Entity.EstadoPublicacion;
import api.Exception.PersonaSinPermisosException;
import api.Exception.PublicacionNoEncontradaException;

import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;


@Repository
public class PublicacionRepository {
  
    private final MongoTemplate mongoTemplate;
    private final PersonaRepository personaRepository;

    private int tamanioPagina = 10;

    public PublicacionRepository(MongoTemplate mongoTemplate, PersonaRepository personaRepository) {
        this.mongoTemplate = mongoTemplate;
        this.personaRepository = personaRepository;
    }

    public boolean existePublicacion(String id) {
        Query query = new Query(new Criteria().andOperator(
                Criteria.where("estaBorrada").is(false),
                Criteria.where("id").is(id)
        ));
        return mongoTemplate.exists(query, Publicacion.class);
    }

    public Publicacion getPublicacion(String publicacionId){
        if(!existePublicacion(publicacionId)){
            throw new PublicacionNoEncontradaException(publicacionId);
        }
        Query query = new Query(Criteria.where("id").is(publicacionId));
        return mongoTemplate.findOne(query, Publicacion.class);
    }

    public List<Publicacion> getAllPublicaciones(){
        Query query = new Query(Criteria.where("estaBorrada").is(false));
        return mongoTemplate.find(query, Publicacion.class);

    }

    public void clear(){
        mongoTemplate.remove(new Query(), Publicacion.class);
    }

    public List<Publicacion> getPublicacionesByJuego(String juegoId){
        Query query = new Query(new Criteria().andOperator(
                Criteria.where("estaBorrada").is(false),
                Criteria.where("juegoId").is(juegoId)));
        return mongoTemplate.find(query, Publicacion.class);
    }

    public void borrarPublicacion(String publicacionId, Persona persona) {
        if(!existePublicacion(publicacionId)){
            throw new PublicacionNoEncontradaException(publicacionId);
        }
        Publicacion publicacion = getPublicacion(publicacionId);
        Persona publicador = personaRepository.getPersonaById(publicacion.getPublicadorId());
        if(publicador.getNombre().equals(persona.getNombre())) {
            publicacion.setEstaBorrada(true);
            publicacion.setEstadoPublicacion(EstadoPublicacion.FINALIZADA);
            mongoTemplate.save(publicacion);
        }
        else{
            throw new PersonaSinPermisosException(publicador.getNombre());
        }
    }

    public List<Publicacion> getPublicacionesByCarta(int cartaId){
        Query query = new Query(new Criteria().andOperator(
                Criteria.where("estaBorrada").is(false),
                Criteria.where("cartaId").is(cartaId)
        ));
        return mongoTemplate.find(query, Publicacion.class);
    }

    public Publicacion guardarPublicacion(Publicacion publicacion){
        publicacion.setEstaBorrada(false);
        mongoTemplate.save(publicacion);
        return publicacion;
    }

    public Publicacion cerrarPublicacion(String id){
        if(!existePublicacion(id)){
            throw new PublicacionNoEncontradaException(id);
        }
        Publicacion publicacion = getPublicacion(id);
        publicacion.setFechaCierre(LocalDateTime.now());
        publicacion.setEstadoPublicacion(EstadoPublicacion.FINALIZADA);
        mongoTemplate.save(publicacion);
        return publicacion;

    }

    public List<Publicacion> getPublicacionesByPersona(String idPersona) {
        Query query = new Query(new Criteria().andOperator(
                Criteria.where("publicadorId").is(idPersona),
                Criteria.where("estaBorrada").is(false)
        ));
        return mongoTemplate.find(query, Publicacion.class);
        
    }


    /**
     * Devuelve las publicaciones aplicando los distintos filtros.
     * Si no se desea usar un filtro en específico, en el DTO se debe marcar ese filtro con null
     * @param filtro un FiltroPublicacionesDTO que contiene todos los filtros a aplicar
     * @return List\<Publicacion\> con todas las publicaciones que cumplen los filtros
     */
    public List<Publicacion> getPublicaciones(FiltroPublicacionesDTO filtro) {
        Criteria criterios = new Criteria();
        if(filtro.cartaId() != null){
            criterios = criterios.and("cartaId").is(filtro.cartaId());
        }
        if(filtro.juegoId() != null){
            criterios = criterios.and("juegoId").is(filtro.juegoId());
        }
        if(filtro.nombrePublicacion() != null){
            criterios = criterios.and("titulo").regex(".*" + Pattern.quote(filtro.nombrePublicacion()) + ".*", "i");
        }
        if(filtro.estadoConservacion() != null){
            criterios = criterios.and("estadoConservacion").is(filtro.estadoConservacion());
        }
        if(filtro.desdeFecha() != null && filtro.hastaFecha() != null){
            criterios = criterios.and("fechaPublicacion").gte(filtro.desdeFecha()).lte(filtro.hastaFecha());
        } else if(filtro.desdeFecha() != null){
            criterios = criterios.and("fechaPublicacion").gte(filtro.desdeFecha());
        }else if(filtro.hastaFecha() != null){
            criterios = criterios.and("fechaPublicacion").lte(filtro.hastaFecha());
        }
        if(filtro.desdePrecio() != null && filtro.hastaPrecio() != null){
            criterios = criterios.and("precio").gte(filtro.desdePrecio()).lte(filtro.hastaPrecio());
        }else if(filtro.desdePrecio() != null){
            criterios = criterios.and("precio").gte(filtro.desdePrecio());
        }else if(filtro.hastaPrecio() != null){
            criterios = criterios.and("precio").lte(filtro.hastaPrecio());
        }
        if(filtro.intereses() != null){
            criterios = criterios.and("intereses").in(filtro.intereses());
        }

        criterios = criterios.and("estaBorrada").is(false);

        Query query = new Query(criterios);
        return mongoTemplate.find(query, Publicacion.class);
    }

    public PseudoPaginacionPublicacionDTO getPublicaciones(FiltroPublicacionesDTO filtro, int pagina) {
        List<Publicacion> publicaciones = getPublicaciones(filtro);

        if(publicaciones.isEmpty()) {
            return new PseudoPaginacionPublicacionDTO(Collections.emptyList(), -1, -1);
        }
        if(pagina < 1 || Math.ceil(publicaciones.size() / (double) tamanioPagina) < pagina ) {
            throw new PaginaInexistenteException(pagina);
        }

        int desde = (pagina - 1) * tamanioPagina ;
        int hasta = Math.min(desde + tamanioPagina, publicaciones.size());

        int pagAnterior = ((pagina - 1) < 1) ? -1 : (pagina - 1);
        int pagSiguinete = (Math.ceil(publicaciones.size() / (double) tamanioPagina) < pagina + 1 ) ? -1 : (pagina + 1);

        List<Publicacion> publicacionesPaginadas = publicaciones.subList(desde,hasta);

        return new PseudoPaginacionPublicacionDTO(publicacionesPaginadas, pagAnterior, pagSiguinete);

    }

    public List<Publicacion> getPublicacionesCreadasByFecha(LocalDate fecha){
        Date desde = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date hasta = Date.from(fecha.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Query query = new Query(Criteria.where("fechaPublicacion").gte(desde).lt(hasta));
        return mongoTemplate.find(query, Publicacion.class);
    }
}

