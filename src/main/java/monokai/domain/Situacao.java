package monokai.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Situacao {
	ATIVO(1),
	INATIVO(0);

	private final int codigo;

	public static Situacao fromCodigo(int codigo) {
		for (Situacao situacao : values()) {
			if (situacao.codigo == codigo) {
				return situacao;
			}
		}
		throw new IllegalArgumentException("Situação inválida: " + codigo);
	}
}
