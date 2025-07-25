package bot.DTO;

import java.time.LocalDateTime;

public record PublicacionPersonaDTO(
        String titulo,
        String imagen,
        String estado,
        String tituloCarta,
        LocalDateTime fechaPublicacion,
        LocalDateTime fechaCierre,
        String refAPublicacion,
        String id
) {
}
