package tpTacs.Bot.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreacionPublicacion {
    private Integer juegoId;
    private Integer cartaId;
    private String titulo;
    private String descripcion;
    private List<Integer> intereses;
    private List<String> imagenes;
    private Float precio;
    private String estadoConservacion;
}
