package api.DTO;


import api.Entity.RolesPersona;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank
        String nombreUsuario,
        @NotBlank
        String contrasenia,
        RolesPersona rolesPersona
) {
}
