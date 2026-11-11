package monokai.service;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import monokai.domain.Usuario;
import monokai.repository.UsuarioRepository;

/**
 * Identifica o usuário autenticado na requisição atual e se ele é um aluno ou
 * um professor. O vínculo é sempre lido do banco, para refletir alterações
 * feitas depois da emissão do token.
 */
@Service
@RequiredArgsConstructor
public class UsuarioLogadoService {

	private final UsuarioRepository usuarioRepository;

	public Usuario getUsuarioLogado() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AuthenticationCredentialsNotFoundException("Nenhum usuário autenticado");
		}
		String username = authentication.getName();
		return usuarioRepository.buscarPorUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
	}

	public boolean isAluno() {
		return getUsuarioLogado().isAluno();
	}

	public boolean isProfessor() {
		return getUsuarioLogado().isProfessor();
	}

	public Optional<Long> getAlunoId() {
		return Optional.ofNullable(getUsuarioLogado().getAlunoId());
	}

	public Optional<Long> getProfessorId() {
		return Optional.ofNullable(getUsuarioLogado().getProfessorId());
	}
}
