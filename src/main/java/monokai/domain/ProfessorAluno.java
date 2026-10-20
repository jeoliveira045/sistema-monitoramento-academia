package monokai.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfessorAluno {

	private Long id;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataAlteracao;
	private Situacao situacao;

	private Professor professor;
	private Aluno aluno;
}
