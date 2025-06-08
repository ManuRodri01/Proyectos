package api.Repository;

import api.Entity.EstadoSolicitud;
import api.Entity.SolicitudCreacionCarta;
import api.Exception.CambioEstadoSolicitudException;
import api.Exception.LimiteDeSolicitudesException;
import api.Exception.SolictudNoEncontradaException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class SolicitudCartaRepository {
    private final MongoTemplate mongoTemplate;

    public SolicitudCartaRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    public void guardarSolicitud(SolicitudCreacionCarta solicitud) {
        Query query = new Query(new Criteria().andOperator(
                Criteria.where("idSolicitante").is(solicitud.getIdSolicitante()),
                Criteria.where("fechaSolicitud").is(solicitud.getFechaSolicitud())
        ));
        List<SolicitudCreacionCarta> solicitudes = mongoTemplate.find(query, SolicitudCreacionCarta.class);
        if(solicitudes.size() < 3){
            mongoTemplate.save(solicitud);
        }
        else{
            throw new LimiteDeSolicitudesException("Carta");
        }
    }

    public SolicitudCreacionCarta obtenerSolicitud(String id) {
        SolicitudCreacionCarta solicitud = mongoTemplate.findById(id, SolicitudCreacionCarta.class);
        if(solicitud == null) {
            throw new SolictudNoEncontradaException(id);
        }
        else{
            return solicitud;
        }
    }

    public List<SolicitudCreacionCarta> obtenerTodasSolicitudes(){
        return mongoTemplate.findAll(SolicitudCreacionCarta.class);
    }

    public List<SolicitudCreacionCarta> obtenerTodasSolicitudesEnEspera(){
        Query query = new Query(Criteria.where("estado").is(EstadoSolicitud.EN_ESPERA));
        return mongoTemplate.find(query, SolicitudCreacionCarta.class);
    }

    public void aceptarSolicitud(SolicitudCreacionCarta solicitud) {
        mongoTemplate.save(solicitud);
        Query solicitudQuery = new Query(new Criteria().andOperator(
                Criteria.where("nombreCarta").is(solicitud.getNombreCarta()),
                Criteria.where("id").ne(solicitud.getId()),
                Criteria.where("idJuego").is(solicitud.getIdJuego())

        ));
        List<SolicitudCreacionCarta> solicitudes = mongoTemplate.find(solicitudQuery, SolicitudCreacionCarta.class);

        for (SolicitudCreacionCarta solicitudCreacionCarta : solicitudes) {
            solicitudCreacionCarta.setEstado(EstadoSolicitud.RECHAZADA);
            solicitudCreacionCarta.setFechaCierre(LocalDate.now());
            mongoTemplate.save(solicitudCreacionCarta);
        }

    }

    public void rechazarSolicitud(String id) {
        SolicitudCreacionCarta solicitud = obtenerSolicitud(id);
        if(solicitud.getEstado() == EstadoSolicitud.EN_ESPERA){
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            solicitud.setFechaCierre(LocalDate.now());
            mongoTemplate.save(solicitud);
        }
        else{
            throw new CambioEstadoSolicitudException();
        }
    }

    public List<SolicitudCreacionCarta> obtenerSolicitudesBySolicitante(String idSolicitante) {
        Query query = new Query(Criteria.where("idSolicitante").is(idSolicitante));
        return mongoTemplate.find(query, SolicitudCreacionCarta.class);
    }

    public void clear(){
        mongoTemplate.remove(new Query(), SolicitudCreacionCarta.class);
    }
}


