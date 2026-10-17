package monokai.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Aluno {

	// Dados vindos do sistema de usuários
	private Long id;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataAlteracao;
	private String nome;
	private String sobrenome;
	private LocalDate dtNascimento;
	private String cpf;
	private String endereco;
	private TipoSanguineo tipoSanguineo;
	private String whatsapp;
	private String email;
	private Situacao situacao;
	private String foto;

	// Dados preenchidos apenas no monitoramento
	private Double peso;
	private Double altura;
	private Double percentualGorduraCorporal;
	private Double imc;
	private String circunferencias;
	private String historicoLesoes;
	private String condicoesMedicas;
	private String usoMedicamentosContinuos;
	private String restricoesMedicas;
	private String parQ;
	private String atestadoMedico;

	private List<Treino> treinos = new ArrayList<>();

	/**
	 * Calcula o IMC (peso em kg / altura em metros ao quadrado) e atualiza o campo imc.
	 * Retorna null quando peso ou altura não estiverem preenchidos.
	 */
	public Double calcularImc() {
		if (peso == null || altura == null || altura <= 0) {
			return null;
		}
		this.imc = peso / (altura * altura);
		return this.imc;
	}

	public void adicionarTreino(Treino treino) {
		treinos.add(treino);
	}
}
