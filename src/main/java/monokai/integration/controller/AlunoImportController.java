package monokai.integration.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import monokai.integration.service.AlunoImportService;

import monokai.domain.Aluno;

@RestController
@RequestMapping("import/alunos")
@AllArgsConstructor
public class AlunoImportController {

	private AlunoImportService alunoImportService;

	@GetMapping
	public List<Aluno> findAll() throws URISyntaxException, IOException, InterruptedException {
		return this.alunoImportService.exec();
	}
}
