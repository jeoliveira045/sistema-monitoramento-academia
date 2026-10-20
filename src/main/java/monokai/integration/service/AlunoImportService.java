package monokai.integration.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import monokai.domain.Aluno;
import monokai.domain.Situacao;
import monokai.integration.domain.AlunoImport;
import monokai.integration.dto.LoginRequest;
import monokai.integration.dto.LoginResponse;
import monokai.repository.AlunoRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class AlunoImportService {

	private final HttpClient httpClient;
	private final AlunoRepository alunoRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();
	@Value("${api.baseurl}")
	private String apiBaseurl;

	public List<Aluno> exec() throws URISyntaxException, IOException, InterruptedException {
		String token = this.token().getToken();

		HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(apiBaseurl + "/alunos"))
				.header("Authorization", "Bearer " + token).header("Accept", "application/json")
				.timeout(Duration.ofSeconds(15)).GET().build();

		HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new IllegalStateException(
					"Falha ao buscar alunos no sistema de usuários: HTTP " + response.statusCode());
		}

		List<AlunoImport> alunosImport = objectMapper.readValue(response.body(),
				new TypeReference<List<AlunoImport>>() {
				});

		return alunosImport.stream().map(this::toAluno).map(alunoRepository::save).toList();
	}

	/**
	 * Converte o AlunoImport no Aluno do monitoramento. Quando o aluno já existe,
	 * parte do registro salvo para não perder os dados preenchidos apenas no
	 * monitoramento.
	 */
	private Aluno toAluno(AlunoImport alunoImport) {
		Aluno aluno = alunoRepository.findById(alunoImport.getId()).orElseGet(Aluno::new);
		aluno.setId(alunoImport.getId());
		aluno.setNome(alunoImport.getNome());
		aluno.setSobrenome(alunoImport.getSobrenome());
		aluno.setDtNascimento(alunoImport.getDtNascimento());
		aluno.setCpf(alunoImport.getCpf());
		aluno.setEndereco(alunoImport.getEndereco());
		aluno.setEmail(alunoImport.getEmail());
		aluno.setWhatsapp(alunoImport.getWhatsapp());
		aluno.setFoto(alunoImport.getFoto());
		aluno.setSituacao(
				alunoImport.getSituacao() == null ? null : Situacao.fromCodigo(alunoImport.getSituacao().getCodigo()));
		return aluno;
	}

	public LoginResponse token() throws URISyntaxException, IOException, InterruptedException {

		String loginRequestString = objectMapper
				.writeValueAsString(new LoginRequest("service-account", "service-account"));

		HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(apiBaseurl + "/auth/login"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(loginRequestString)).build();

		HttpResponse<String> response = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new IllegalStateException("Falha no login da service-account: HTTP " + response.statusCode());
		}

		LoginResponse loginResponse = objectMapper.readValue(response.body(), LoginResponse.class);
		if (loginResponse.getToken() == null || loginResponse.getToken().isBlank()) {
			throw new IllegalStateException("Login da service-account não retornou token");
		}

		return loginResponse;
	}
}
