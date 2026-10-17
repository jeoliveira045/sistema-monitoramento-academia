package monokai.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Exercicio {

	private Long id;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataAlteracao;
	private String nomeExercicio;
	private String musculo;
}
