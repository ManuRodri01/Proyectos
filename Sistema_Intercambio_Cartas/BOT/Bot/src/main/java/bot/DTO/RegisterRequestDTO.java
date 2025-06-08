package bot.DTO;

import jakarta.validation.constraints.NotBlank;
import bot.RolesPersona;

public record RegisterRequestDTO(
        @NotBlank
        String nombreUsuario,
        @NotBlank
        String contrasenia,
        RolesPersona rolesPersona
) {
}
