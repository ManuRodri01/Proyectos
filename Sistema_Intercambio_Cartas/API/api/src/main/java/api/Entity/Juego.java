package api.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "juegos")
public class Juego {
    @Id
    private String id;
    private String nombre;
    private boolean estaBorrado;

    public Juego(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

}
