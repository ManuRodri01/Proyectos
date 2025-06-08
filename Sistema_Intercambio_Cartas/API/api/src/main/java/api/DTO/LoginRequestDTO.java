package api.DTO;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank
        String nombreUsuario,
        @NotBlank
        String contrasenia
) {
}
