package monokai.service;

import monokai.domain.Aluno;
import monokai.repository.AlunoRepository;
import monokai.repository.ItemExercicioRepository;
import monokai.repository.TreinoRepository;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AlunoService {

	private AlunoRepository alunoRepository;
	private ItemExercicioRepository itemExercicioRepository;
	private TreinoRepository treinoRepository;

	public List<Aluno> findAll() {
		return this.alunoRepository.findAll();
	}

	public Aluno findById(Long id) {

		var aluno = alunoRepository.findById(id);
		aluno.ifPresentOrElse(aluno1 -> {
			var treinoList = treinoRepository.findByAlunoId(aluno1.getId());
			treinoList.stream().forEach(treino -> {
				var itemTreino = itemExercicioRepository.findByTreinoId(treino.getId());
				if (itemTreino.isEmpty())
					throw new RuntimeException("Itens de treino não encontrados");
				treino.setItens(itemTreino);
			});
			aluno1.setTreinos(treinoList);
		}, () -> {
			throw new RuntimeException("Aluno não encontrado!");
		});

		return aluno.get();
	}

}
