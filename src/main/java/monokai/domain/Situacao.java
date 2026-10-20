package monokai.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Situacao {
	ATIVO(1),
	INATIVO(2);

	private static final Map<Integer, Situacao> POR_CODIGO = Arrays.stream(values())
			.collect(Collectors.toUnmodifiableMap(Situacao::getCodigo, Function.identity()));

	private final int codigo;

	public static Situacao fromCodigo(int codigo) {
		Situacao situacao = POR_CODIGO.get(codigo);
		if (situacao == null) {
			throw new IllegalArgumentException("Codigo de Situação inválido: " + codigo);
		}
		return situacao;
	}
}
