package grupo1.tpTACS_API.DTO;

import grupo1.tpTACS_API.Entity.RolesPersona;

public record PersonaDTO(
        String nombre,
        RolesPersona rol
) {
}
