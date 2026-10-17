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
import monokai.domain.Exercicio;
import monokai.domain.ItemExercicio;
import monokai.domain.TipoExercicio;

/**
 * Carrega o item com o exercício completo.
 */
@Repository
@RequiredArgsConstructor
public class ItemExercicioRepository {

	private static final String SELECT = """
			SELECT ie.*,
				e.data_criacao AS e_data_criacao,
				e.data_alteracao AS e_data_alteracao,
				e.nome_exercicio AS e_nome_exercicio,
				e.musculo AS e_musculo
			FROM item_exercicio ie
			JOIN exercicio e ON e.id = ie.exercicio_id
			""";

	private final JdbcClient jdbcClient;

	public Optional<ItemExercicio> findById(Long id) {
		return jdbcClient.sql(SELECT + " WHERE ie.id = :id")
				.param("id", id)
				.query(this::mapRow)
				.optional();
	}

	public List<ItemExercicio> findByTreinoId(Long treinoId) {
		return jdbcClient.sql(SELECT + " WHERE ie.treino_id = :treinoId ORDER BY ie.id")
				.param("treinoId", treinoId)
				.query(this::mapRow)
				.list();
	}

	public ItemExercicio save(Long treinoId, ItemExercicio item) {
		LocalDateTime agora = LocalDateTime.now();
		Long exercicioId = item.getExercicio() == null ? null : item.getExercicio().getId();
		String tipoExercicio = item.getTipoExercicio() == null ? null : item.getTipoExercicio().name();
		if (item.getId() == null) {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcClient.sql("""
					INSERT INTO item_exercicio (
						data_criacao, data_alteracao, treino_id, exercicio_id,
						serie, repeticoes, tipo_exercicio
					) VALUES (
						:agora, :agora, :treinoId, :exercicioId,
						:serie, :repeticoes, :tipoExercicio
					)
					""")
					.param("agora", agora)
					.param("treinoId", treinoId)
					.param("exercicioId", exercicioId)
					.param("serie", item.getSerie())
					.param("repeticoes", item.getRepeticoes())
					.param("tipoExercicio", tipoExercicio)
					.update(keyHolder, "id");
			item.setId(keyHolder.getKeyAs(Long.class));
			item.setDataCriacao(agora);
		} else {
			jdbcClient.sql("""
					UPDATE item_exercicio
					SET data_alteracao = :agora, treino_id = :treinoId, exercicio_id = :exercicioId,
						serie = :serie, repeticoes = :repeticoes, tipo_exercicio = :tipoExercicio
					WHERE id = :id
					""")
					.param("id", item.getId())
					.param("agora", agora)
					.param("treinoId", treinoId)
					.param("exercicioId", exercicioId)
					.param("serie", item.getSerie())
					.param("repeticoes", item.getRepeticoes())
					.param("tipoExercicio", tipoExercicio)
					.update();
		}
		item.setDataAlteracao(agora);
		return item;
	}

	public void deleteById(Long id) {
		jdbcClient.sql("DELETE FROM item_exercicio WHERE id = :id")
				.param("id", id)
				.update();
	}

	private ItemExercicio mapRow(ResultSet rs, int rowNum) throws SQLException {
		ItemExercicio item = new ItemExercicio();
		item.setId(rs.getLong("id"));
		item.setDataCriacao(rs.getObject("data_criacao", LocalDateTime.class));
		item.setDataAlteracao(rs.getObject("data_alteracao", LocalDateTime.class));
		item.setSerie(rs.getObject("serie", Integer.class));
		item.setRepeticoes(rs.getObject("repeticoes", Integer.class));
		String tipoExercicio = rs.getString("tipo_exercicio");
		item.setTipoExercicio(tipoExercicio == null ? null : TipoExercicio.valueOf(tipoExercicio));

		Exercicio exercicio = new Exercicio();
		exercicio.setId(rs.getLong("exercicio_id"));
		exercicio.setDataCriacao(rs.getObject("e_data_criacao", LocalDateTime.class));
		exercicio.setDataAlteracao(rs.getObject("e_data_alteracao", LocalDateTime.class));
		exercicio.setNomeExercicio(rs.getString("e_nome_exercicio"));
		exercicio.setMusculo(rs.getString("e_musculo"));
		item.setExercicio(exercicio);
		return item;
	}
}
