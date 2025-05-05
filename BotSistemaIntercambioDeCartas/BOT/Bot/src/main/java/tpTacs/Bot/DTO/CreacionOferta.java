package tpTacs.Bot.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreacionOferta {
    private Integer publicacionId;
    private List<Integer> cartas;
    private Float valor;
}
