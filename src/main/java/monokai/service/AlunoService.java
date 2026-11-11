package monokai.service;

import monokai.domain.Aluno;
import monokai.domain.ItemExercicio;
import monokai.domain.Situacao;
import monokai.domain.Usuario;
import monokai.repository.AlunoRepository;
import monokai.repository.ItemExercicioRepository;
import monokai.repository.TreinoRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AlunoService {

	private AlunoRepository alunoRepository;
	private ItemExercicioRepository itemExercicioRepository;
	private TreinoRepository treinoRepository;
	private UsuarioLogadoService usuarioLogadoService;

	/**
	 * Professor vê apenas os seus alunos ativos e aluno vê apenas os próprios
	 * dados. Os demais usuários (admin, service) veem todos, podendo filtrar por
	 * situação.
	 */
	public List<Aluno> findAll(Situacao situacao) {
		Usuario usuario = usuarioLogadoService.getUsuarioLogado();
		if (usuario.isProfessor()) {
			return alunoRepository.findByProfessorIdAndSituacao(usuario.getProfessorId(), Situacao.ATIVO);
		}
		if (usuario.isAluno()) {
			return List.of(findById(usuario.getAlunoId()));
		}
		if (situacao != null) {
			return alunoRepository.findBySituacao(situacao);
		}
		return alunoRepository.findAll();
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

	public Aluno save(Aluno aluno) {

		var treino = aluno.getTreinos();
		var alunoSalvo = alunoRepository.save(aluno);
		treino.forEach(treino1 -> {
			var treinoSalvo = treinoRepository.save(alunoSalvo.getId(), treino1);
			var itemExercicioList = treino1.getItens();
			var itemExercicioSalvo = new ArrayList<ItemExercicio>();
			itemExercicioList.forEach(item -> {
				itemExercicioSalvo.add(itemExercicioRepository.save(treinoSalvo.getId(), item));
			});
			treino1.setItens(itemExercicioSalvo);
			alunoSalvo.adicionarTreino(treino1);
		});
		return aluno;

	}
}
