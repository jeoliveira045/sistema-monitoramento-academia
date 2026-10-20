package monokai.integration.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import monokai.domain.Professor;
import monokai.integration.service.ProfessorImportService;

@RestController
@RequestMapping("import/professores")
@AllArgsConstructor
public class ProfessorImportController {

	private ProfessorImportService professorImportService;

	@GetMapping
	public List<Professor> findAll() throws URISyntaxException, IOException, InterruptedException {
		return this.professorImportService.exec();
	}
}
