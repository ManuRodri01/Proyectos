package api.Service;


import api.DTO.FiltroPublicacionesDTO;
import api.Entity.Carta;
import api.Entity.Publicacion;
import api.Entity.EstadoConservacion;
import api.Repository.PublicacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public class PublicacionServiceTest {

    @Autowired
    private PublicacionService publicacionService;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private Environment env;


    @DynamicPropertySource
    static void overrideMongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }


    private Carta pikachu = new Carta("1","pikachu", "1");
    private Carta voltorb = new Carta("2","voltorb", "1");
    private Carta charizard = new Carta("3","Charizard", "1");
    private Carta magoOscuro = new Carta("4","MagoOscuro", "2");




    @BeforeEach
    void setUp() {
        publicacionRepository.clear();

        Publicacion publicacion = new Publicacion("1","", Arrays.asList("3","4"),400f, EstadoConservacion.NUEVA, LocalDateTime.now());
        publicacion.setJuegoId("1");
        Publicacion publicacion2 = new Publicacion("3","charizard es lo mas", Arrays.asList("1","2"),1000f, EstadoConservacion.NUEVA,LocalDateTime.now());
        publicacion2.setJuegoId("1");
        Publicacion publicacion3 = new Publicacion("2","", Arrays.asList(),100f, EstadoConservacion.CASINUEVA,LocalDateTime.now());
        publicacion3.setJuegoId("1");
        Publicacion publicacion4 = new Publicacion("3","Este charizard esta impecable", Arrays.asList("4"),800f, EstadoConservacion.CASINUEVA,LocalDateTime.now());
        publicacion4.setJuegoId("1");
        Publicacion publicacion5 = new Publicacion("4","", Arrays.asList("3","1"),250f, EstadoConservacion.CASINUEVA,LocalDateTime.now().minusDays(2));
        publicacion5.setJuegoId("2");
        publicacionRepository.guardarPublicacion(publicacion);
        publicacionRepository.guardarPublicacion(publicacion2);
        publicacionRepository.guardarPublicacion(publicacion3);
        publicacionRepository.guardarPublicacion(publicacion4);
        publicacionRepository.guardarPublicacion(publicacion5);
    }






    @Test
    public void filtroPorIdTest() {
        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO("1",null,null,null,null,null,null,null,null);
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(1,listaFiltrada.size());
        assertEquals("1",listaFiltrada.get(0).getCartaId());
    }

    @Test
    public void filtroPorEstadoConservacionTest() {

        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(null,null,null, EstadoConservacion.NUEVA,null,null,null,null,null);
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(2,listaFiltrada.size());

    }

    @Test
    public void filtroPorEstadoConservacionYIdTest() {
        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO("3",null,null, EstadoConservacion.CASINUEVA,null,null,null,null,null);
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(1,listaFiltrada.size());
    }

    @Test
    public void filtroPorJuegoTest() {
        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(null,"1",null,null,null,null,null,null,null);
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
        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(null,null,null,null,null,null,null,null,Arrays.asList(magoOscuro.getId()));
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(2,listaFiltrada.size());

        filtro = new FiltroPublicacionesDTO(null,null,null,null,null,null,null,null,Arrays.asList(magoOscuro.getId(), charizard.getId()));
        listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(3,listaFiltrada.size());
    }

    @Test
    public void filtroPorNombreTest() {
        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(null,null,"charizard es lo",null,null,null,null,null,null);
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(1,listaFiltrada.size());
    }

    @Test
    public void testFiltroPublicacion() {

        Publicacion publicacion6 = new Publicacion("3","Este charizard es un asco", Arrays.asList("2"),600f, EstadoConservacion.CASINUEVA,LocalDateTime.now());
        publicacion6.setJuegoId("1");
        Publicacion publicacion7 = new Publicacion("3","Este charizard es un asco", Arrays.asList("2","4"),750f, EstadoConservacion.CASINUEVA,LocalDateTime.now().minusDays(2));
        publicacion7.setJuegoId("1");
        Publicacion publicacion8 = new Publicacion("3","Este charizard esta bien", Arrays.asList("1"),700f, EstadoConservacion.CASINUEVA,LocalDateTime.now());
        publicacion8.setJuegoId("1");
        publicacionRepository.guardarPublicacion(publicacion6);
        publicacionRepository.guardarPublicacion(publicacion7);
        publicacionRepository.guardarPublicacion(publicacion8);

        FiltroPublicacionesDTO filtro = new FiltroPublicacionesDTO(null,"1","charizard", EstadoConservacion.CASINUEVA,null,LocalDateTime.now().minusDays(1),650f,900f,Arrays.asList(magoOscuro.getId()));
        List<Publicacion> listaFiltrada = publicacionService.getPublicaciones(filtro);
        assertEquals(1,listaFiltrada.size());
        assertEquals(publicacion7.getPrecio(),listaFiltrada.get(0).getPrecio());
    }


}
