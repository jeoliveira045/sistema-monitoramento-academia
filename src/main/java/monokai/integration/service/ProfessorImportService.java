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
import monokai.domain.Professor;
import monokai.domain.Situacao;
import monokai.integration.domain.ProfessorImport;
import monokai.integration.dto.LoginRequest;
import monokai.integration.dto.LoginResponse;
import monokai.repository.ProfessorRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ProfessorImportService {

	private final HttpClient httpClient;
	private final ProfessorRepository professorRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();
	@Value("${api.baseurl}")
	private String apiBaseurl;

	public List<Professor> exec() throws URISyntaxException, IOException, InterruptedException {
		String token = this.token().getToken();

		HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(apiBaseurl + "/professores"))
				.header("Authorization", "Bearer " + token).header("Accept", "application/json")
				.timeout(Duration.ofSeconds(15)).GET().build();

		HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			throw new IllegalStateException(
					"Falha ao buscar professores no sistema de usuários: HTTP " + response.statusCode());
		}

		List<ProfessorImport> professoresImport = objectMapper.readValue(response.body(),
				new TypeReference<List<ProfessorImport>>() {
				});

		return professoresImport.stream().map(this::toProfessor).map(professorRepository::save).toList();
	}

	/**
	 * Converte o ProfessorImport no Professor do monitoramento. Quando o professor já existe, parte do
	 * registro salvo para não perder os dados que não vêm do sistema de usuários (whatsapp e foto).
	 */
	private Professor toProfessor(ProfessorImport professorImport) {
		Professor professor = professorRepository.findById(professorImport.getId()).orElseGet(Professor::new);
		professor.setId(professorImport.getId());
		professor.setNome(professorImport.getNome());
		professor.setSobrenome(professorImport.getSobrenome());
		professor.setDtNascimento(professorImport.getDtNascimento());
		professor.setCpf(professorImport.getCpf());
		professor.setCref(professorImport.getCref());
		professor.setEmail(professorImport.getEmail());
		professor.setSituacao(professorImport.getSituacao() == null ? null
				: Situacao.fromCodigo(professorImport.getSituacao().getCodigo()));
		return professor;
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
