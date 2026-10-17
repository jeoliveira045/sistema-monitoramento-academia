package monokai.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import monokai.domain.Treino;
import monokai.domain.TreinoPeriodoTipo;
import monokai.domain.TreinoTipo;

/**
 * Persiste apenas os dados do treino. Os itens são persistidos pelo
 * ItemExercicioRepository.
 */
@Repository
@RequiredArgsConstructor
public class TreinoRepository {

	private final JdbcClient jdbcClient;

	public Optional<Treino> findById(Long id) {
		return jdbcClient.sql("SELECT * FROM treino WHERE id = :id").param("id", id).query(this::mapRow).optional();
	}

	public List<Treino> findByAlunoId(Long alunoId) {
		return jdbcClient.sql("SELECT * FROM treino WHERE aluno_id = :alunoId ORDER BY data_criacao DESC")
				.param("alunoId", alunoId).query(this::mapRow).list();
	}

	public Treino save(Long alunoId, Treino treino) {
		LocalDateTime agora = LocalDateTime.now();
		if (treino.getId() == null) {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcClient
					.sql("""
							INSERT INTO treino (data_criacao, data_alteracao, aluno_id, treino_periodo, tipo, treino_mes_vigente)
							VALUES (:agora, :agora, :alunoId, :treinoPeriodo, :tipo, :treinoMesVigente)
							""")
					.param("agora", agora).param("alunoId", alunoId)
					.param("treinoPeriodo", treino.getTreinoPeriodo() == null ? null : treino.getTreinoPeriodo().name())
					.param("tipo", treino.getTipo() == null ? null : treino.getTipo().name())
					.param("treinoMesVigente", treino.getTreinoMesVigente()).update(keyHolder, "id");
			treino.setId(keyHolder.getKeyAs(Long.class));
			treino.setDataCriacao(agora);
		} else {
			jdbcClient.sql("""
					UPDATE treino
						SET data_alteracao = :agora, aluno_id = :alunoId, treino_periodo = :treinoPeriodo,
						tipo = :tipo, treino_mes_vigente = :treinoMesVigente
					WHERE id = :id
					""").param("id", treino.getId()).param("agora", agora).param("alunoId", alunoId)
					.param("treinoPeriodo", treino.getTreinoPeriodo() == null ? null : treino.getTreinoPeriodo().name())
					.param("tipo", treino.getTipo() == null ? null : treino.getTipo().name())
					.param("treinoMesVigente", treino.getTreinoMesVigente()).update();
		}
		treino.setDataAlteracao(agora);
		return treino;
	}

	public void deleteById(Long id) {
		jdbcClient.sql("DELETE FROM treino WHERE id = :id").param("id", id).update();
	}

	private Treino mapRow(ResultSet rs, int rowNum) throws SQLException {
		Treino treino = new Treino();
		treino.setId(rs.getLong("id"));
		treino.setDataCriacao(rs.getObject("data_criacao", LocalDateTime.class));
		treino.setDataAlteracao(rs.getObject("data_alteracao", LocalDateTime.class));
		String treinoPeriodo = rs.getString("treino_periodo");
		treino.setTreinoPeriodo(treinoPeriodo == null ? null : TreinoPeriodoTipo.valueOf(treinoPeriodo));
		String tipo = rs.getString("tipo");
		treino.setTipo(tipo == null ? null : TreinoTipo.valueOf(tipo));
		treino.setTreinoMesVigente(rs.getString("treino_mes_vigente"));
		return treino;
	}
}
