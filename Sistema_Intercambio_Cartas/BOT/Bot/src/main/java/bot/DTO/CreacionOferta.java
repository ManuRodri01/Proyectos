package bot.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreacionOferta {
    private String publicacionId;
    private List<String> cartas;
    private Float valor;
}
