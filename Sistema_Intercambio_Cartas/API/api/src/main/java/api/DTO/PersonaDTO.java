package api.DTO;

import api.Entity.RolesPersona;

public record PersonaDTO(
        String nombre,
        RolesPersona rol
) {
}
