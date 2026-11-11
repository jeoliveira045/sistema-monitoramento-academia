
package monokai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
class ConcreteUserDetails implements UserDetails {

	private final String username;
	private final String password;
	private final Collection<? extends GrantedAuthority> authorities;
	private final Long alunoId;
	private final Long professorId;

	Long getAlunoId() {
		return alunoId;
	}

	Long getProfessorId() {
		return professorId;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}
}
