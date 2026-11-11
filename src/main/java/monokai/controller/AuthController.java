
package monokai.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import monokai.domain.Usuario;
import monokai.service.JwtService;
import monokai.service.UsuarioLogadoService;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final UsuarioLogadoService usuarioLogadoService;

  public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
      @Qualifier("listUserDetailService") UserDetailsService userDetailsService,
      UsuarioLogadoService usuarioLogadoService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
    this.usuarioLogadoService = usuarioLogadoService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

    UserDetails user = userDetailsService.loadUserByUsername(request.username());
    String token = jwtService.generateToken(user);

    return ResponseEntity.ok(new LoginResponse(token));
  }

  @GetMapping("/me")
  public ResponseEntity<UsuarioLogadoResponse> me() {
    Usuario usuario = usuarioLogadoService.getUsuarioLogado();
    return ResponseEntity.ok(new UsuarioLogadoResponse(usuario.getUsername(), usuario.isAluno(),
        usuario.isProfessor(), usuario.getAlunoId(), usuario.getProfessorId()));
  }
}

record LoginRequest(String username, String password) {
}

record LoginResponse(String token) {
}

record UsuarioLogadoResponse(String username, boolean aluno, boolean professor, Long alunoId, Long professorId) {
}
