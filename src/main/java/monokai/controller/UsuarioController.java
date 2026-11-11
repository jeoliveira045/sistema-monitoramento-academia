package monokai.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import monokai.domain.Role;
import monokai.domain.Usuario;
import monokai.repository.UsuarioRepository;
import monokai.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

	private final UsuarioService usuarioService;
	private final UsuarioRepository usuarioRepository;

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
	public ResponseEntity<UsuarioResponse> criar(@RequestBody CriarUsuarioRequest request) {
		Usuario usuario = usuarioService.criar(request.username(), request.senha(), request.alunoId(),
				request.professorId());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new UsuarioResponse(usuario.getId(), usuario.getUsername(),
						usuario.getRoles().stream().map(Role::getRole).toList(), usuario.getAlunoId(),
						usuario.getProfessorId()));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'SERVICE')")
	public ResponseEntity<?> findAll() {
		return ResponseEntity.ok(usuarioRepository.findAll().stream().map(usuario -> {
			return new UsuarioResponse(
					usuario.getId(),
					usuario.getUsername(),
					usuario.getRoles().stream().map(Role::getRole).toList(),
					usuario.getAlunoId(),
					usuario.getProfessorId()
			);
		}));
	}
}

record CriarUsuarioRequest(String username, String senha, Long alunoId, Long professorId) {
}

record UsuarioResponse(Long id, String username, List<String> roles, Long alunoId, Long professorId) {
}
