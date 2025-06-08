package bot.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreacionPublicacion {
    private String juegoId;
    private String cartaId;
    private String titulo;
    private String descripcion;
    private List<String> intereses;
    private List<String> imagenes;
    private Float precio;
    private String estadoConservacion;
}
