package monokai.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import monokai.domain.Professor;
import monokai.domain.Situacao;
import monokai.repository.ProfessorRepository;

/**
 * Os professores vêm do sistema de usuários (ver ProfessorImportController),
 * por isso este controller expõe apenas a consulta.
 */
@RestController
@RequestMapping("/professores")
@RequiredArgsConstructor
public class ProfessorController {

	private final ProfessorRepository professorRepository;

	@GetMapping
	public ResponseEntity<List<Professor>> findAll(@RequestParam(required = false) Situacao situacao) {
		if (situacao != null) {
			return ResponseEntity.ok(professorRepository.findBySituacao(situacao));
		}
		return ResponseEntity.ok(professorRepository.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Professor> findById(@PathVariable Long id) {
		return professorRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}
}
