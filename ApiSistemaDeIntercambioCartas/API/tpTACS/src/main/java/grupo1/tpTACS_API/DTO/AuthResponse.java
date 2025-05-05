package grupo1.tpTACS_API.DTO;

import grupo1.tpTACS_API.Entity.RolesPersona;

public record AuthResponse(
        String token,
        RolesPersona rol
) {
}
