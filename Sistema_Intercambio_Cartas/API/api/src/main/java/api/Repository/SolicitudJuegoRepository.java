package api.Repository;

import api.Entity.EstadoSolicitud;
import api.Entity.SolicitudCreacionJuego;
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
public class SolicitudJuegoRepository {
    private final MongoTemplate mongoTemplate;

    public SolicitudJuegoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void guardarSolicitud(SolicitudCreacionJuego solicitud) {
        Query query = new Query(new Criteria().andOperator(
                Criteria.where("idSolicitante").is(solicitud.getIdSolicitante()),
                Criteria.where("fechaSolicitud").is(solicitud.getFechaSolicitud())
        ));
        List<SolicitudCreacionJuego> solicitudes = mongoTemplate.find(query, SolicitudCreacionJuego.class);
        if(solicitudes.size() < 3){
            mongoTemplate.save(solicitud);
        }
        else{
            throw new LimiteDeSolicitudesException("Juego");
        }
    }

    public SolicitudCreacionJuego obtenerSolicitud(String id) {
        SolicitudCreacionJuego solicitud = mongoTemplate.findById(id, SolicitudCreacionJuego.class);
        if(solicitud == null) {
            throw new SolictudNoEncontradaException(id);
        }
        else{
            return solicitud;
        }
    }

    public List<SolicitudCreacionJuego> obtenerTodasSolicitudes(){
        return mongoTemplate.findAll(SolicitudCreacionJuego.class);
    }

    public List<SolicitudCreacionJuego> obtenerTodasSolicitudesEnEspera(){
        Query query = new Query(Criteria.where("estado").is(EstadoSolicitud.EN_ESPERA));
        return mongoTemplate.find(query, SolicitudCreacionJuego.class);
    }

    public void aceptarSolicitud(SolicitudCreacionJuego solicitud) {
        mongoTemplate.save(solicitud);
        Query solicitudQuery = new Query(new Criteria().andOperator(
                Criteria.where("nombreJuego").is(solicitud.getNombreJuego()),
                Criteria.where("id").ne(solicitud.getId())

        ));
        List<SolicitudCreacionJuego> solicitudes = mongoTemplate.find(solicitudQuery, SolicitudCreacionJuego.class);

        for (SolicitudCreacionJuego solicitudCreacionJuego : solicitudes) {
            solicitudCreacionJuego.setEstado(EstadoSolicitud.RECHAZADA);
            solicitudCreacionJuego.setFechaCierre(LocalDate.now());
            mongoTemplate.save(solicitudCreacionJuego);
        }

    }

    public void rechazarSolicitud(String id) {
        SolicitudCreacionJuego solicitud = obtenerSolicitud(id);
        if(solicitud.getEstado() == EstadoSolicitud.EN_ESPERA){
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            solicitud.setFechaCierre(LocalDate.now());
            mongoTemplate.save(solicitud);
        }
        else{
            throw new CambioEstadoSolicitudException();
        }

    }

    public List<SolicitudCreacionJuego> obtenerSolicitudesBySolicitante(String idSolicitante) {
        Query query = new Query(Criteria.where("idSolicitante").is(idSolicitante));
        return mongoTemplate.find(query, SolicitudCreacionJuego.class);
    }

    public void clear(){
        mongoTemplate.remove(new Query(), SolicitudCreacionJuego.class);
    }
}
