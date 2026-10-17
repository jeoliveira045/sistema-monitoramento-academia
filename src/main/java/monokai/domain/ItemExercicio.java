package monokai.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemExercicio {

	private Long id;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataAlteracao;
	private Integer serie;
	private Integer repeticoes;
	private TipoExercicio tipoExercicio;

	private Exercicio exercicio;
}
