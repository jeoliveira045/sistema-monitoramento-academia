package monokai.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Professor {

	// Dados vindos do sistema de usuários
	private Long id;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataAlteracao;
	private String nome;
	private String sobrenome;
	private LocalDate dtNascimento;
	private String cpf;
	private String cref;
	private String whatsapp;
	private String email;
	private Situacao situacao;
	private String foto;

	private List<ProfessorAluno> alunos = new ArrayList<>();

	public void adicionarAluno(ProfessorAluno professorAluno) {
		alunos.add(professorAluno);
	}
}
