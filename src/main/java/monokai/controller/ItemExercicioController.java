package monokai.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import monokai.domain.ItemExercicio;
import monokai.repository.ItemExercicioRepository;
import monokai.repository.TreinoRepository;

@RestController
@RequestMapping("/treinos/{treinoId}/itens")
@RequiredArgsConstructor
public class ItemExercicioController {

	private final TreinoRepository treinoRepository;
	private final ItemExercicioRepository itemExercicioRepository;

	@GetMapping
	public ResponseEntity<List<ItemExercicio>> findByTreino(@PathVariable Long treinoId) {
		validarTreino(treinoId);
		return ResponseEntity.ok(itemExercicioRepository.findByTreinoId(treinoId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ItemExercicio> findById(@PathVariable Long treinoId, @PathVariable Long id) {
		return ResponseEntity.of(buscarItemDoTreino(treinoId, id));
	}

	@PostMapping
	public ResponseEntity<ItemExercicio> create(@PathVariable Long treinoId, @RequestBody ItemExercicio item) {
		validarTreino(treinoId);
		item.setId(null);
		ItemExercicio salvo = itemExercicioRepository.save(treinoId, item);
		return ResponseEntity.status(HttpStatus.CREATED).body(itemExercicioRepository.findById(salvo.getId()).orElseThrow());
	}

	@PutMapping("/{id}")
	public ResponseEntity<ItemExercicio> update(@PathVariable Long treinoId, @PathVariable Long id,
			@RequestBody ItemExercicio item) {
		if (buscarItemDoTreino(treinoId, id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		item.setId(id);
		itemExercicioRepository.save(treinoId, item);
		return ResponseEntity.of(itemExercicioRepository.findById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long treinoId, @PathVariable Long id) {
		if (buscarItemDoTreino(treinoId, id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		itemExercicioRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	private void validarTreino(Long treinoId) {
		if (treinoRepository.findById(treinoId).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Treino não encontrado");
		}
	}

	private Optional<ItemExercicio> buscarItemDoTreino(Long treinoId, Long id) {
		return itemExercicioRepository.findByTreinoId(treinoId).stream()
				.filter(item -> item.getId().equals(id))
				.findFirst();
	}
}
