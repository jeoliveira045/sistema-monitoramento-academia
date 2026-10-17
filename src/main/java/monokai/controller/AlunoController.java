package monokai.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import monokai.domain.Aluno;
import monokai.domain.Situacao;
import monokai.repository.AlunoRepository;
import monokai.service.AlunoService;

/**
 * O id do aluno vem do sistema de usuários, por isso o POST exige o id no corpo
 * e o save faz um upsert.
 */
@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
public class AlunoController {

	private final AlunoRepository alunoRepository;
	private final AlunoService alunoService;

	@GetMapping
	public ResponseEntity<List<Aluno>> findAll(@RequestParam(required = false) Situacao situacao) {
		if (situacao != null) {
			return ResponseEntity.ok(alunoRepository.findBySituacao(situacao));
		}
		return ResponseEntity.ok(alunoService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Aluno> findById(@PathVariable Long id) {
		return ResponseEntity.ok(alunoService.findById(id));
	}

	@PostMapping
	public ResponseEntity<Aluno> create(@RequestBody Aluno aluno) {
		if (aluno.getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O id do aluno deve vir do sistema de usuários");
		}
		aluno.calcularImc();
		return ResponseEntity.status(HttpStatus.CREATED).body(alunoService.save(aluno));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Aluno> update(@PathVariable Long id, @RequestBody Aluno aluno) {
		if (alunoRepository.findById(id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		aluno.setId(id);
		aluno.calcularImc();
		return ResponseEntity.ok(alunoService.save(aluno));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		if (alunoRepository.findById(id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		alunoRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
