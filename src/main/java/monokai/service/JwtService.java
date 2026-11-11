
package monokai.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

      @Value("${jwt.secret}")
      private String jwtSecret;

      @Value("${jwt.expiration}")
      private long jwtExpiration;

      private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
      }

      public String generateToken(UserDetails user) {
        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        log.info("Gerando token JWT para o usuário: {}", user.getUsername());
        JwtBuilder builder = Jwts.builder().subject(user.getUsername()).claim("roles", roles);
        if (user instanceof ConcreteUserDetails details) {
          // Claims nulas são ignoradas pelo jjwt, então só vai para o token o vínculo que existir
          builder.claim("alunoId", details.getAlunoId()).claim("professorId", details.getProfessorId());
        }
        return builder.issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSigningKey()).compact();
      }
}
