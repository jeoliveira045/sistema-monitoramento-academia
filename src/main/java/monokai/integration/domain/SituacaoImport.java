
package monokai.integration.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SituacaoImport {
	ATIVO(1), INATIVO(2);

	private static final Map<Integer, SituacaoImport> POR_CODIGO = Arrays.stream(values())
			.collect(Collectors.toUnmodifiableMap(SituacaoImport::getCodigo, Function.identity()));

	private final int codigo;

	public static SituacaoImport fromCodigo(int codigo) {
		SituacaoImport situacao = POR_CODIGO.get(codigo);
		if (situacao == null) {
			throw new IllegalArgumentException("Codigo de Situação inválido: " + codigo);
		}
		return situacao;
	}
}
