package api.DTO;

import java.time.LocalDate;

public record FechaIntervaloDTO(
		LocalDate desde,
		LocalDate hasta
) {
}
