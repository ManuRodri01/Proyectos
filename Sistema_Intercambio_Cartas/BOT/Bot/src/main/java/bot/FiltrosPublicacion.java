package bot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FiltrosPublicacion {
    private String cartaId;
    private String juegoId;
    private String nombrePublicacion;
    private String estadoConservacion;
    private LocalDateTime desdeFecha;
    private LocalDateTime hastaFecha;
    private Float desdePrecio;
    private Float hastaPrecio;
    private List<String> intereses;


}
