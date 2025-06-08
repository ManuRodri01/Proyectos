package api.Config;

import api.DTO.CreacionJuegoDTO;
import api.Entity.*;
import api.Repository.*;
import api.Service.AuthService;
import api.DTO.RegisterRequestDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Profile("!test")
public class DataSeeder {

    private final JuegoRepository juegoRepository;
    private final CartaRepository cartaRepository;
    private final PublicacionRepository publicacionRepository;
    private final PersonaRepository personaRepository;
    private final OfertaRepository ofertaRepository;
    private final AuthService authService;
    private final MongoTemplate mongoTemplate;

    private final List<String> nombresUsuarios = new ArrayList<>();
    private final Map<String, String> credenciales = new HashMap<>();

    public DataSeeder(JuegoRepository juegoRepository,
                      CartaRepository cartaRepository,
                      PublicacionRepository publicacionRepository,
                      PersonaRepository personaRepository,
                      OfertaRepository ofertaRepository,
                      AuthService authService,
                      MongoTemplate mongoTemplate) {
        this.juegoRepository = juegoRepository;
        this.cartaRepository = cartaRepository;
        this.publicacionRepository = publicacionRepository;
        this.personaRepository = personaRepository;
        this.ofertaRepository = ofertaRepository;
        this.authService = authService;
        this.mongoTemplate = mongoTemplate;
    }


    @PostConstruct
    public void seedData() {
        if(!mongoTemplate.collectionExists(ComprobadorDataSeeder.class)){
            ComprobadorDataSeeder comprobadorDataSeeder = new ComprobadorDataSeeder();
            comprobadorDataSeeder.setEstaCargado(true);
            mongoTemplate.save(comprobadorDataSeeder);
            cartaRepository.clear();
            publicacionRepository.clear();
            personaRepository.clear();
            ofertaRepository.clear();
            juegoRepository.clear();

            // Crear juegos
            List<Juego> juegos = List.of(
                    juegoRepository.guardarJuego(new CreacionJuegoDTO( "Pokemon")),
                    juegoRepository.guardarJuego(new CreacionJuegoDTO( "Yu-Gi-Oh!")),
                    juegoRepository.guardarJuego(new CreacionJuegoDTO( "Magic"))
            );

            // Crear 45 cartas (15 por juego)
            List<Carta> todasLasCartas = new ArrayList<>();
            int idCarta = 1;
            for (Juego juego : juegos) {
                for (int i = 1; i <= 15; i++) {
                    Carta carta = new Carta("" + juego.getNombre() + " - Carta " + i, juego.getId());
                    cartaRepository.guardarCarta(carta);
                    todasLasCartas.add(carta);
                }
            }

            // Crear 5 usuarios
            for (int i = 1; i <= 5; i++) {
                String username = "user" + i;
                String password = "pass" + i;
                authService.register(new RegisterRequestDTO(username, password, RolesPersona.USUARIO));
                nombresUsuarios.add(username);
                credenciales.put(username, password);
            }

            //Crear un admin
            String adminName = "admin1";
            String adminPassword = "adminPass";
            authService.register(new RegisterRequestDTO(adminName, adminPassword, RolesPersona.ADMIN));
            credenciales.put(adminName, adminPassword);

            Random random = new Random();

            // Crear 10 publicaciones con 1-3 ofertas
            for (int i = 1; i <= 10; i++) {
                Persona publicador = personaRepository.getPersona(nombresUsuarios.get(random.nextInt(nombresUsuarios.size())));
                Carta carta = todasLasCartas.get(random.nextInt(todasLasCartas.size()));
                List<Carta> intereses = List.of(todasLasCartas.get(random.nextInt(todasLasCartas.size())));

                Publicacion pub = new Publicacion(
                        "Publicación " + i,
                        publicador.getId(),
                        carta.getId(),
                        "Descripción de prueba",
                        intereses.stream().map(Carta::getId).toList(),
                        List.of("imagen-demo.jpg"),
                        random.nextFloat(100),
                        EstadoConservacion.values()[random.nextInt(EstadoConservacion.values().length)],
                        LocalDateTime.now().minusDays(random.nextInt(10)),
                        null
                );
                pub.setJuegoId(carta.getJuegoId());
                pub.setEstadoPublicacion(EstadoPublicacion.ABIERTA);

                publicacionRepository.guardarPublicacion(pub);

                // Crear entre 1 y 3 ofertas
                int cantidadOfertas = 1 + random.nextInt(3);
                for (int j = 0; j < cantidadOfertas; j++) {
                    Persona ofertante;
                    do {
                        ofertante = personaRepository.getPersona(nombresUsuarios.get(random.nextInt(nombresUsuarios.size())));
                    } while (ofertante.getNombre().equals(publicador.getNombre()));

                    List<Carta> cartasOferta = List.of(todasLasCartas.get(random.nextInt(todasLasCartas.size())));
                    Oferta oferta = new Oferta(
                            EstadoOferta.EN_ESPERA,
                            pub.getId(),
                            LocalDate.now().minusDays(random.nextInt(5)),
                            ofertante.getId()
                    );
                    oferta.setPublicadorId(publicador.getId());
                    oferta.setCartas(cartasOferta.stream().map(Carta::getId).toList());
                    oferta.setValor(random.nextFloat(100));
                    ofertaRepository.guardarOferta(oferta);
                }
            }

            // Mostrar credenciales creadas
            System.out.println("\n======= USUARIOS DE PRUEBA =======");
            credenciales.forEach((usuario, clave) ->
                    System.out.println("Usuario: " + usuario + " | Contraseña: " + clave)
            );
            System.out.println("==================================\n");
        }


    }


}
