package bot.DTO;

import java.time.LocalDate;

public record OfertaPersonaDTO(
        String nombreCarta,
        String estado,
        String tituloPublicacion,
        String nombreOfertante,
        LocalDate fecha,
        String imagen,
        String refAOferta,
        String id
) {
}
