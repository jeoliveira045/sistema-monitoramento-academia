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

import lombok.RequiredArgsConstructor;
import monokai.domain.Exercicio;
import monokai.repository.ExercicioRepository;

@RestController
@RequestMapping("/exercicios")
@RequiredArgsConstructor
public class ExercicioController {

	private final ExercicioRepository exercicioRepository;

	@GetMapping
	public ResponseEntity<List<Exercicio>> findAll(@RequestParam(required = false) String musculo) {
		if (musculo != null) {
			return ResponseEntity.ok(exercicioRepository.findByMusculo(musculo));
		}
		return ResponseEntity.ok(exercicioRepository.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Exercicio> findById(@PathVariable Long id) {
		return ResponseEntity.of(exercicioRepository.findById(id));
	}

	@PostMapping
	public ResponseEntity<Exercicio> create(@RequestBody Exercicio exercicio) {
		exercicio.setId(null);
		return ResponseEntity.status(HttpStatus.CREATED).body(exercicioRepository.save(exercicio));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Exercicio> update(@PathVariable Long id, @RequestBody Exercicio exercicio) {
		return exercicioRepository.findById(id)
				.map(existente -> {
					exercicio.setId(id);
					exercicio.setDataCriacao(existente.getDataCriacao());
					return ResponseEntity.ok(exercicioRepository.save(exercicio));
				})
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		if (exercicioRepository.findById(id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		exercicioRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
