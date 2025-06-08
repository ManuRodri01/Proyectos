package api.DTO;

import java.time.LocalDate;

public record VisitaDTO(
		String PersonaId,
		LocalDate fecha
) {
}
