package monokai.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Usuario {

	private Long id;
	private String username;
	private String senha;
	private List<Role> roles;

	// Um usuário pode estar associado a um aluno ou a um professor, nunca aos dois
	private Long alunoId;
	private Long professorId;

	public boolean isAluno() {
		return alunoId != null;
	}

	public boolean isProfessor() {
		return professorId != null;
	}
}
