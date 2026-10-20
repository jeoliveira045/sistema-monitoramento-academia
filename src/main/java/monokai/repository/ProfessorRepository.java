package monokai.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import monokai.domain.Professor;
import monokai.domain.Situacao;

/**
 * Persiste apenas os dados do professor. O relacionamento com os alunos fica
 * na tabela professor_aluno.
 */
@Repository
@RequiredArgsConstructor
public class ProfessorRepository {

	private final JdbcClient jdbcClient;

	public List<Professor> findAll() {
		return jdbcClient.sql("SELECT * FROM professor ORDER BY nome, sobrenome").query(this::mapRow).list();
	}

	public Optional<Professor> findById(Long id) {
		return jdbcClient.sql("SELECT * FROM professor WHERE id = :id").param("id", id).query(this::mapRow)
				.optional();
	}

	public List<Professor> findBySituacao(Situacao situacao) {
		return jdbcClient.sql("SELECT * FROM professor WHERE situacao = :situacao ORDER BY nome, sobrenome")
				.param("situacao", situacao.getCodigo()).query(this::mapRow).list();
	}

	/**
	 * O id do professor vem do sistema de usuários, por isso o save faz um upsert:
	 * insere quando o professor ainda não existe e atualiza quando já existe.
	 */
	public Professor save(Professor professor) {
		if (professor.getId() == null) {
			throw new IllegalArgumentException("O id do professor deve vir do sistema de usuários");
		}
		LocalDateTime agora = LocalDateTime.now();
		jdbcClient.sql("""
				INSERT INTO professor (
					id, data_criacao, data_alteracao, nome, sobrenome, dt_nascimento, cpf, cref,
					whatsapp, email, situacao, foto
				) VALUES (
					:id, :agora, :agora, :nome, :sobrenome, :dtNascimento, :cpf, :cref,
					:whatsapp, :email, :situacao, :foto
				)
				ON CONFLICT (id) DO UPDATE SET
					data_alteracao = EXCLUDED.data_alteracao,
					nome = EXCLUDED.nome,
					sobrenome = EXCLUDED.sobrenome,
					dt_nascimento = EXCLUDED.dt_nascimento,
					cpf = EXCLUDED.cpf,
					cref = EXCLUDED.cref,
					whatsapp = EXCLUDED.whatsapp,
					email = EXCLUDED.email,
					situacao = EXCLUDED.situacao,
					foto = EXCLUDED.foto
				""").param("id", professor.getId()).param("agora", agora).param("nome", professor.getNome())
				.param("sobrenome", professor.getSobrenome()).param("dtNascimento", professor.getDtNascimento())
				.param("cpf", professor.getCpf()).param("cref", professor.getCref())
				.param("whatsapp", professor.getWhatsapp()).param("email", professor.getEmail())
				.param("situacao", professor.getSituacao() == null ? null : professor.getSituacao().getCodigo())
				.param("foto", professor.getFoto()).update();
		return findById(professor.getId()).orElseThrow();
	}

	public void deleteById(Long id) {
		jdbcClient.sql("DELETE FROM professor WHERE id = :id").param("id", id).update();
	}

	private Professor mapRow(ResultSet rs, int rowNum) throws SQLException {
		Professor professor = new Professor();
		professor.setId(rs.getLong("id"));
		professor.setDataCriacao(rs.getObject("data_criacao", LocalDateTime.class));
		professor.setDataAlteracao(rs.getObject("data_alteracao", LocalDateTime.class));
		professor.setNome(rs.getString("nome"));
		professor.setSobrenome(rs.getString("sobrenome"));
		professor.setDtNascimento(rs.getObject("dt_nascimento", LocalDate.class));
		professor.setCpf(rs.getString("cpf"));
		professor.setCref(rs.getString("cref"));
		professor.setWhatsapp(rs.getString("whatsapp"));
		professor.setEmail(rs.getString("email"));
		Integer situacao = rs.getObject("situacao", Integer.class);
		professor.setSituacao(situacao == null ? null : Situacao.fromCodigo(situacao));
		professor.setFoto(rs.getString("foto"));
		return professor;
	}
}
