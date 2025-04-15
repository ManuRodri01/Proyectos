package manuRodri.intercambioCartas.Service;

import manuRodri.intercambioCartas.DTO.FiltroPublicacionesDTO;
import manuRodri.intercambioCartas.Entity.Carta;
import manuRodri.intercambioCartas.Entity.Juego;
import manuRodri.intercambioCartas.Entity.Publicacion;
import manuRodri.intercambioCartas.Entity.estadoConservacion;
import manuRodri.intercambioCartas.Repository.PublicacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PublicacionServiceTest {
    @Autowired
    private PublicacionService publicacionService;

    @Autowired
    private PublicacionRepository publicacionRepository;

    private Juego pokemon = new Juego(1,"Pokemon");
    private Juego yugi = new Juego(2,"YugiOH");
    private Carta pikachu = new Carta(1,"pikachu", pokemon);
    private Carta voltorb = new Carta(2,"voltorb", pokemon);
    private Carta charizard = new Carta(3,"Charizard", pokemon);
    private Carta magoOscuro = new Carta(4,"MagoOscuro", yugi);

    @BeforeEach
    void setUp() {
        publicacionRepository.clear();

        Publicacion publicacion = new Publicacion(pikachu,"", Arrays.asList(charizard,magoOscuro),400f, estadoConservacion.NUEVA, LocalDateTime.now());
        Publicacion publicacion2 = new Publicacion(charizard,"charizard es lo mas", Arrays.asList(pikachu,voltorb),1000f, estadoConservacion.NUEVA,LocalDateTime.now());
        Publicacion publicacion3 = new Publicacion(voltorb,"", Arrays.asList(),100f, estadoConservacion.CASINUEVA,LocalDateTime.now());
        Publicacion publicacion4 = new Publicacion(charizard,"Este charizard esta impecable", Arrays.asList(magoOscuro),800f, estadoConservacion.CASINUEVA,LocalDateTime.now());
        Publicacion publicacion5 = new Publicacion(magoOscuro,"", Arrays.asList(charizard,pikachu),250f, estadoConservacion.CASINUEVA,LocalDateTime.now().minusDays(2));
        publicacionRepository.guardarPublicacion(publicacion);
        publicacionRepository.guardarPublicacion(publicacion2);
        publicacionRepository.guardarPublicacion(publicacion3);
        publicacionRepository.guardarPublicacion(publicacion4);
        publicacionRepository.guardarPublicacion(publicacion5);
    }

    @Test
    public void filtroPorIdTest() {
        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(1,null,null,null,null,null,null,null,null);
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(1,listaFiltrada.size());
        assertEquals(1,listaFiltrada.get(0).getCarta().getId());
    }

    @Test
    public void filtroPorEstadoConservacionTest() {

        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(null,null,null,estadoConservacion.NUEVA,null,null,null,null,null);
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(2,listaFiltrada.size());

    }

    @Test
    public void filtroPorEstadoConservacionYIdTest() {
        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(3,null,null,estadoConservacion.CASINUEVA,null,null,null,null,null);
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(1,listaFiltrada.size());
    }

    @Test
    public void filtroPorJuegoTest() {
        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(null,1,null,null,null,null,null,null,null);
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(4,listaFiltrada.size());
    }

    @Test
    public void filtroPorFechaTest() {
        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(null,null,null,null,null,LocalDateTime.now().minusDays(1),null,null,null);
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(1,listaFiltrada.size());
    }

    @Test
    public void filtroPorPrecioTest() {
        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(null,null,null,null,null,null,500f,null,null);
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(2,listaFiltrada.size());
    }

    @Test
    public void filtroPorPrecio2Test() {
        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(null,null,null,null,null,null,500f,800f,null);
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(1,listaFiltrada.size());
    }

    @Test
    public void filtroPorInteresesTest() {
        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(null,null,null,null,null,null,null,null,Arrays.asList(magoOscuro));
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(2,listaFiltrada.size());

        filtro = new FiltroPublicacionesDTO(null,null,null,null,null,null,null,null,Arrays.asList(magoOscuro, charizard));
        listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(3,listaFiltrada.size());
    }

    @Test
    public void filtroPorDescripcionTest() {
        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(null,null,"charizard es lo",null,null,null,null,null,null);
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(1,listaFiltrada.size());
    }

    @Test
    public void testFiltroPublicacion() {

        Publicacion publicacion6 = new Publicacion(charizard,"Este charizard es un asco", Arrays.asList(voltorb),600f, estadoConservacion.CASINUEVA,LocalDateTime.now());
        Publicacion publicacion7 = new Publicacion(charizard,"Este charizard es un asco", Arrays.asList(voltorb,magoOscuro),750f, estadoConservacion.CASINUEVA,LocalDateTime.now().minusDays(2));
        Publicacion publicacion8 = new Publicacion(charizard,"Este charizard esta bien", Arrays.asList(pikachu),700f, estadoConservacion.CASINUEVA,LocalDateTime.now());
        publicacionRepository.guardarPublicacion(publicacion6);
        publicacionRepository.guardarPublicacion(publicacion7);
        publicacionRepository.guardarPublicacion(publicacion8);

        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(null,1,"charizard",estadoConservacion.CASINUEVA,null,LocalDateTime.now().minusDays(1),650f,900f,Arrays.asList(magoOscuro));
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(1,listaFiltrada.size());
        assertEquals(publicacion7,listaFiltrada.get(0));
    }
}
