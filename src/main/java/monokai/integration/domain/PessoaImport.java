package monokai.integration.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PessoaImport {

	private Long id;
	private LocalDateTime dataCriacao;
	private LocalDateTime dataAlteracao;
	private String nome;
	private String sobrenome;
	private LocalDate dtNascimento;
	private String cpf;
	private String endereco;
	private String email;
	private GeneroImport genero;
	private SituacaoImport situacao;

	public String getNomeCompleto() {
		return nome + " " + sobrenome;
	}
}
