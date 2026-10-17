package monokai.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Treino {

	private Long id;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataAlteracao;
	private TreinoPeriodoTipo treinoPeriodo;
	private TreinoTipo tipo;
	private String treinoMesVigente;

	private List<ItemExercicio> itens = new ArrayList<>();

	public void adicionarItem(ItemExercicio item) {
		itens.add(item);
	}
}
