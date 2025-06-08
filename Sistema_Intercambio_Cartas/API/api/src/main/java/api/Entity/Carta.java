package api.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "cartas")
public class Carta {
    @Id
    private String id;
    private String nombre;
    private String juegoId;
    private boolean estaBorrada;



    public Carta(String id, String nombre, String juegoId) {
        this.id = id;
        this.nombre = nombre;
        this.juegoId = juegoId;
    }

    public Carta(String nombre, String juegoId) {
        this.nombre = nombre;
        this.juegoId = juegoId;
    }

}
