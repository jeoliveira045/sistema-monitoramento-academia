package monokai.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import monokai.domain.Role;
import monokai.domain.Usuario;
import monokai.repository.AlunoRepository;
import monokai.repository.ProfessorRepository;
import monokai.repository.RoleRepository;
import monokai.repository.UsuarioRepository;

/**
 * Cria usuários associados a um aluno ou a um professor. A role do usuário é
 * definida pela pessoa associada: ALUNO quando alunoId é informado e PROFESSOR
 * quando professorId é informado.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

	private static final String ROLE_ALUNO = "ALUNO";
	private static final String ROLE_PROFESSOR = "PROFESSOR";

	private final UsuarioRepository usuarioRepository;
	private final RoleRepository roleRepository;
	private final AlunoRepository alunoRepository;
	private final ProfessorRepository professorRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Usuario criar(String username, String senha, Long alunoId, Long professorId) {
		log.info("Criando usuário: {}", username);
		if (username == null || username.isBlank() || senha == null || senha.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username e senha são obrigatórios");
		}
		if ((alunoId == null) == (professorId == null)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Informe apenas um entre alunoId e professorId");
		}
		if (usuarioRepository.existePorUsername(username)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já existe na base: " + username);
		}

		String role = alunoId != null ? validarAluno(alunoId) : validarProfessor(professorId);

		Usuario usuario = new Usuario();
		usuario.setUsername(username);
		usuario.setSenha(passwordEncoder.encode(senha));
		usuario.setAlunoId(alunoId);
		usuario.setProfessorId(professorId);
		usuario.setRoles(List.of(buscarRole(role)));
		usuarioRepository.salvar(usuario);

		log.info("Usuário {} criado com a role {}", username, role);
		return usuario;
	}

	private String validarAluno(Long alunoId) {
		if (alunoRepository.findById(alunoId).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado: " + alunoId);
		}
		if (usuarioRepository.buscarPorAlunoId(alunoId).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Aluno já possui usuário: " + alunoId);
		}
		return ROLE_ALUNO;
	}

	private String validarProfessor(Long professorId) {
		if (professorRepository.findById(professorId).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado: " + professorId);
		}
		if (usuarioRepository.buscarPorProfessorId(professorId).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Professor já possui usuário: " + professorId);
		}
		return ROLE_PROFESSOR;
	}

	private Role buscarRole(String role) {
		return roleRepository.buscarPorRole(role).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role não encontrada: " + role));
	}
}
