package grupo1.tpTACS_API.Config;

import grupo1.tpTACS_API.DTO.CreacionJuegoDTO;
import grupo1.tpTACS_API.Entity.*;
import grupo1.tpTACS_API.Repository.*;
import grupo1.tpTACS_API.Service.AuthService;
import grupo1.tpTACS_API.DTO.RegisterRequestDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataSeeder {

    private final JuegoRepository juegoRepository;
    private final CartaRepository cartaRepository;
    private final PublicacionRepository publicacionRepository;
    private final PersonaRepository personaRepository;
    private final OfertaRepository ofertaRepository;
    private final AuthService authService;

    private final List<String> nombresUsuarios = new ArrayList<>();
    private final Map<String, String> credenciales = new HashMap<>();

    public DataSeeder(JuegoRepository juegoRepository,
                      CartaRepository cartaRepository,
                      PublicacionRepository publicacionRepository,
                      PersonaRepository personaRepository,
                      OfertaRepository ofertaRepository,
                      AuthService authService) {
        this.juegoRepository = juegoRepository;
        this.cartaRepository = cartaRepository;
        this.publicacionRepository = publicacionRepository;
        this.personaRepository = personaRepository;
        this.ofertaRepository = ofertaRepository;
        this.authService = authService;
    }

    @PostConstruct
    public void seedData() {
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
                Carta carta = new Carta("" + juego.getNombre() + " - Carta " + i, juego);
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

        String adminName = "admin1";
        String adminPassword = "adminPass";
        authService.register(new RegisterRequestDTO(adminName, adminPassword, RolesPersona.ADMIN));
        nombresUsuarios.add(adminName);
        credenciales.put(adminName, adminPassword);

        Random random = new Random();

        // Crear 10 publicaciones con 1-3 ofertas
        for (int i = 1; i <= 10; i++) {
            Persona publicador = personaRepository.getPersona(nombresUsuarios.get(random.nextInt(nombresUsuarios.size())));
            Carta carta = todasLasCartas.get(random.nextInt(todasLasCartas.size()));
            List<Carta> intereses = List.of(todasLasCartas.get(random.nextInt(todasLasCartas.size())));

            Publicacion pub = new Publicacion(
                    "Publicación " + i,
                    publicador,
                    carta,
                    "Descripción de prueba",
                    intereses,
                    List.of("https://assets.pokemon.com/static-assets/content-assets/cms2-es-xl/img/cards/web/XY1/XY1_LA_42.png"),
                    random.nextFloat(100),
                    EstadoConservacion.values()[random.nextInt(EstadoConservacion.values().length)],
                    LocalDateTime.now().minusDays(random.nextInt(10)),
                    null
            );

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
                        pub,
                        LocalDate.now().minusDays(random.nextInt(5)),
                        ofertante
                );
                oferta.setCartas(cartasOferta);
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
