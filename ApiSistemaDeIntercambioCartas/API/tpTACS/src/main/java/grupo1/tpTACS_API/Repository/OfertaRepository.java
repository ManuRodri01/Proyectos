package grupo1.tpTACS_API.Repository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import grupo1.tpTACS_API.Entity.Oferta;
import org.springframework.stereotype.Repository;


@Repository
public class OfertaRepository {

	private Map<Integer, Oferta> tablaOfertas = new HashMap<>();
	private AtomicInteger idOferta = new AtomicInteger(0);
	public OfertaRepository(){

	}

	public boolean existeOferta(int id) {
		return tablaOfertas.containsKey(id);
	}

	public Oferta guardarOferta(Oferta oferta){
		oferta.setId(idOferta.getAndIncrement());
		tablaOfertas.put(oferta.getId(), oferta);
		return oferta;
	}

	public Oferta getOferta(int id){
		return tablaOfertas.get(id);
	}

	public Oferta borrarOferta(int id){
		return tablaOfertas.remove(id);
	}

	public List<Oferta> getOfertasAPublicacion(int publicacionId){
		List<Oferta> ofertas = new ArrayList<>();

		for (Oferta oferta : tablaOfertas.values()) {
			if(oferta.getPublicacion().getId() == publicacionId) {
				ofertas.add(oferta);
			}
		}
		return ofertas;
	}

	public List<Oferta> getOfertasHechasByPersona(String nombre) {
		List<Oferta> ofertas = new ArrayList<>();

		for (Oferta oferta : tablaOfertas.values()) {
			if(oferta.getOfertante().getNombre().equals(nombre)) {
				ofertas.add(oferta);
			}
		}
		return ofertas;
	}

    public List<Oferta> getOfertasRecibidasByPersona(String nombre){
		List<Oferta> ofertas = new ArrayList<>();

		for (Oferta oferta : tablaOfertas.values()) {
			if(oferta.getPublicacion().getPublicador().getNombre().equals(nombre)) {
				ofertas.add(oferta);
			}
		}
        return ofertas;
    }

}
