package grupo1.tpTACS_API.DTO;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank
        String nombreUsuario,
        @NotBlank
        String contrasenia
) {
}
