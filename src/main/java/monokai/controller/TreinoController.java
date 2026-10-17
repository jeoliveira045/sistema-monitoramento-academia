package monokai.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import monokai.domain.Treino;
import monokai.repository.AlunoRepository;
import monokai.repository.ItemExercicioRepository;
import monokai.repository.TreinoRepository;

/**
 * Treinos ficam aninhados ao aluno. O POST aceita os itens junto com o treino;
 * depois de criado, os itens são mantidos pelo ItemExercicioController.
 */
@RestController
@RequestMapping("/alunos/{alunoId}/treinos")
@RequiredArgsConstructor
public class TreinoController {

	private final AlunoRepository alunoRepository;
	private final TreinoRepository treinoRepository;
	private final ItemExercicioRepository itemExercicioRepository;

	@GetMapping
	public ResponseEntity<List<Treino>> findByAluno(@PathVariable Long alunoId) {
		validarAluno(alunoId);
		List<Treino> treinos = treinoRepository.findByAlunoId(alunoId);
		treinos.forEach(this::carregarItens);
		return ResponseEntity.ok(treinos);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Treino> findById(@PathVariable Long alunoId, @PathVariable Long id) {
		return ResponseEntity.of(buscarTreinoDoAluno(alunoId, id).map(this::carregarItens));
	}

	@PostMapping
	@Transactional
	public ResponseEntity<Treino> create(@PathVariable Long alunoId, @RequestBody Treino treino) {
		validarAluno(alunoId);
		treino.setId(null);
		Treino salvo = treinoRepository.save(alunoId, treino);
		for (ItemExercicio item : salvo.getItens()) {
			item.setId(null);
			itemExercicioRepository.save(salvo.getId(), item);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(carregarItens(salvo));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Treino> update(@PathVariable Long alunoId, @PathVariable Long id,
			@RequestBody Treino treino) {
		return buscarTreinoDoAluno(alunoId, id)
				.map(existente -> {
					treino.setId(id);
					treino.setDataCriacao(existente.getDataCriacao());
					return ResponseEntity.ok(carregarItens(treinoRepository.save(alunoId, treino)));
				})
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long alunoId, @PathVariable Long id) {
		if (buscarTreinoDoAluno(alunoId, id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		treinoRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	private void validarAluno(Long alunoId) {
		if (alunoRepository.findById(alunoId).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado");
		}
	}

	private Optional<Treino> buscarTreinoDoAluno(Long alunoId, Long id) {
		return treinoRepository.findByAlunoId(alunoId).stream()
				.filter(treino -> treino.getId().equals(id))
				.findFirst();
	}

	private Treino carregarItens(Treino treino) {
		treino.setItens(itemExercicioRepository.findByTreinoId(treino.getId()));
		return treino;
	}
}
