package api.DTO;

import api.Entity.RolesPersona;

public record AuthResponse(
        String token,
        RolesPersona rol
) {
}
