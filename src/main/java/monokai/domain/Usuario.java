package monokai.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Usuario {

	private Long id;
	private String username;
	private String senha;
	private List<Role> roles;
}
