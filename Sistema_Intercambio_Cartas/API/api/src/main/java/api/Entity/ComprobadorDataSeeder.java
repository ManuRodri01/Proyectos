package api.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@NoArgsConstructor
@Document(collection = "comprobadador")
public class ComprobadorDataSeeder {
    @Id
    String id;
    boolean estaCargado;
}
