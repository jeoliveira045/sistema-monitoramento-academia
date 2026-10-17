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
import monokai.domain.Aluno;
import monokai.domain.Situacao;
import monokai.domain.TipoSanguineo;

@Repository
@RequiredArgsConstructor
public class AlunoRepository {

	private final JdbcClient jdbcClient;

	public List<Aluno> findAll() {
		return jdbcClient.sql("SELECT * FROM aluno ORDER BY nome, sobrenome").query(this::mapRow).list();
	}

	public Optional<Aluno> findById(Long id) {
		return jdbcClient.sql("SELECT * FROM aluno WHERE id = :id").param("id", id).query(this::mapRow).optional();
	}

	public List<Aluno> findBySituacao(Situacao situacao) {
		return jdbcClient.sql("SELECT * FROM aluno WHERE situacao = :situacao ORDER BY nome, sobrenome")
				.param("situacao", situacao.getCodigo()).query(this::mapRow).list();
	}

	/**
	 * O id do aluno vem do sistema de usuários, por isso o save faz um upsert:
	 * insere quando o aluno ainda não existe e atualiza quando já existe.
	 */
	public Aluno save(Aluno aluno) {
		if (aluno.getId() == null) {
			throw new IllegalArgumentException("O id do aluno deve vir do sistema de usuários");
		}
		LocalDateTime agora = LocalDateTime.now();
		jdbcClient.sql("""
				INSERT INTO aluno (
					id, data_criacao, data_alteracao, nome, sobrenome, dt_nascimento, cpf, endereco,
					tipo_sanguineo, whatsapp, email, situacao, foto, peso, altura,
					percentual_gordura_corporal, imc, circunferencias, historico_lesoes, condicoes_medicas,
					uso_medicamentos_continuos, restricoes_medicas, par_q, atestado_medico
				) VALUES (
					:id, :agora, :agora, :nome, :sobrenome, :dtNascimento, :cpf, :endereco,
					:tipoSanguineo, :whatsapp, :email, :situacao, :foto, :peso, :altura,
					:percentualGorduraCorporal, :imc, :circunferencias, :historicoLesoes, :condicoesMedicas,
					:usoMedicamentosContinuos, :restricoesMedicas, :parQ, :atestadoMedico
				)
				ON CONFLICT (id) DO UPDATE SET
					data_alteracao = EXCLUDED.data_alteracao,
					nome = EXCLUDED.nome,
					sobrenome = EXCLUDED.sobrenome,
					dt_nascimento = EXCLUDED.dt_nascimento,
					cpf = EXCLUDED.cpf,
					endereco = EXCLUDED.endereco,
					tipo_sanguineo = EXCLUDED.tipo_sanguineo,
					whatsapp = EXCLUDED.whatsapp,
					email = EXCLUDED.email,
					situacao = EXCLUDED.situacao,
					foto = EXCLUDED.foto,
					peso = EXCLUDED.peso,
					altura = EXCLUDED.altura,
					percentual_gordura_corporal = EXCLUDED.percentual_gordura_corporal,
					imc = EXCLUDED.imc,
					circunferencias = EXCLUDED.circunferencias,
					historico_lesoes = EXCLUDED.historico_lesoes,
					condicoes_medicas = EXCLUDED.condicoes_medicas,
					uso_medicamentos_continuos = EXCLUDED.uso_medicamentos_continuos,
					restricoes_medicas = EXCLUDED.restricoes_medicas,
					par_q = EXCLUDED.par_q,
					atestado_medico = EXCLUDED.atestado_medico
				""").param("id", aluno.getId()).param("agora", agora).param("nome", aluno.getNome())
				.param("sobrenome", aluno.getSobrenome()).param("dtNascimento", aluno.getDtNascimento())
				.param("cpf", aluno.getCpf()).param("endereco", aluno.getEndereco())
				.param("tipoSanguineo", aluno.getTipoSanguineo() == null ? null : aluno.getTipoSanguineo().name())
				.param("whatsapp", aluno.getWhatsapp()).param("email", aluno.getEmail())
				.param("situacao", aluno.getSituacao() == null ? null : aluno.getSituacao().getCodigo())
				.param("foto", aluno.getFoto()).param("peso", aluno.getPeso()).param("altura", aluno.getAltura())
				.param("percentualGorduraCorporal", aluno.getPercentualGorduraCorporal()).param("imc", aluno.getImc())
				.param("circunferencias", aluno.getCircunferencias())
				.param("historicoLesoes", aluno.getHistoricoLesoes())
				.param("condicoesMedicas", aluno.getCondicoesMedicas())
				.param("usoMedicamentosContinuos", aluno.getUsoMedicamentosContinuos())
				.param("restricoesMedicas", aluno.getRestricoesMedicas()).param("parQ", aluno.getParQ())
				.param("atestadoMedico", aluno.getAtestadoMedico()).update();
		return findById(aluno.getId()).orElseThrow();
	}

	public void deleteById(Long id) {
		jdbcClient.sql("DELETE FROM aluno WHERE id = :id").param("id", id).update();
	}

	private Aluno mapRow(ResultSet rs, int rowNum) throws SQLException {
		Aluno aluno = new Aluno();
		aluno.setId(rs.getLong("id"));
		aluno.setDataCriacao(rs.getObject("data_criacao", LocalDateTime.class));
		aluno.setDataAlteracao(rs.getObject("data_alteracao", LocalDateTime.class));
		aluno.setNome(rs.getString("nome"));
		aluno.setSobrenome(rs.getString("sobrenome"));
		aluno.setDtNascimento(rs.getObject("dt_nascimento", LocalDate.class));
		aluno.setCpf(rs.getString("cpf"));
		aluno.setEndereco(rs.getString("endereco"));
		String tipoSanguineo = rs.getString("tipo_sanguineo");
		aluno.setTipoSanguineo(tipoSanguineo == null ? null : TipoSanguineo.valueOf(tipoSanguineo));
		aluno.setWhatsapp(rs.getString("whatsapp"));
		aluno.setEmail(rs.getString("email"));
		Integer situacao = rs.getObject("situacao", Integer.class);
		aluno.setSituacao(situacao == null ? null : Situacao.fromCodigo(situacao));
		aluno.setFoto(rs.getString("foto"));
		aluno.setPeso(rs.getObject("peso", Double.class));
		aluno.setAltura(rs.getObject("altura", Double.class));
		aluno.setPercentualGorduraCorporal(rs.getObject("percentual_gordura_corporal", Double.class));
		aluno.setImc(rs.getObject("imc", Double.class));
		aluno.setCircunferencias(rs.getString("circunferencias"));
		aluno.setHistoricoLesoes(rs.getString("historico_lesoes"));
		aluno.setCondicoesMedicas(rs.getString("condicoes_medicas"));
		aluno.setUsoMedicamentosContinuos(rs.getString("uso_medicamentos_continuos"));
		aluno.setRestricoesMedicas(rs.getString("restricoes_medicas"));
		aluno.setParQ(rs.getString("par_q"));
		aluno.setAtestadoMedico(rs.getString("atestado_medico"));
		return aluno;
	}
}
