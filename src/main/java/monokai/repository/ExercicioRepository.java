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

@Repository
@RequiredArgsConstructor
public class ExercicioRepository {

	private final JdbcClient jdbcClient;

	public List<Exercicio> findAll() {
		return jdbcClient.sql("SELECT * FROM exercicio ORDER BY nome_exercicio")
				.query(this::mapRow)
				.list();
	}

	public Optional<Exercicio> findById(Long id) {
		return jdbcClient.sql("SELECT * FROM exercicio WHERE id = :id")
				.param("id", id)
				.query(this::mapRow)
				.optional();
	}

	public List<Exercicio> findByMusculo(String musculo) {
		return jdbcClient.sql("SELECT * FROM exercicio WHERE musculo = :musculo ORDER BY nome_exercicio")
				.param("musculo", musculo)
				.query(this::mapRow)
				.list();
	}

	public Exercicio save(Exercicio exercicio) {
		LocalDateTime agora = LocalDateTime.now();
		if (exercicio.getId() == null) {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcClient.sql("""
					INSERT INTO exercicio (data_criacao, data_alteracao, nome_exercicio, musculo)
					VALUES (:agora, :agora, :nomeExercicio, :musculo)
					""")
					.param("agora", agora)
					.param("nomeExercicio", exercicio.getNomeExercicio())
					.param("musculo", exercicio.getMusculo())
					.update(keyHolder, "id");
			exercicio.setId(keyHolder.getKeyAs(Long.class));
			exercicio.setDataCriacao(agora);
		} else {
			jdbcClient.sql("""
					UPDATE exercicio
					SET data_alteracao = :agora, nome_exercicio = :nomeExercicio, musculo = :musculo
					WHERE id = :id
					""")
					.param("id", exercicio.getId())
					.param("agora", agora)
					.param("nomeExercicio", exercicio.getNomeExercicio())
					.param("musculo", exercicio.getMusculo())
					.update();
		}
		exercicio.setDataAlteracao(agora);
		return exercicio;
	}

	public void deleteById(Long id) {
		jdbcClient.sql("DELETE FROM exercicio WHERE id = :id")
				.param("id", id)
				.update();
	}

	private Exercicio mapRow(ResultSet rs, int rowNum) throws SQLException {
		Exercicio exercicio = new Exercicio();
		exercicio.setId(rs.getLong("id"));
		exercicio.setDataCriacao(rs.getObject("data_criacao", LocalDateTime.class));
		exercicio.setDataAlteracao(rs.getObject("data_alteracao", LocalDateTime.class));
		exercicio.setNomeExercicio(rs.getString("nome_exercicio"));
		exercicio.setMusculo(rs.getString("musculo"));
		return exercicio;
	}
}
