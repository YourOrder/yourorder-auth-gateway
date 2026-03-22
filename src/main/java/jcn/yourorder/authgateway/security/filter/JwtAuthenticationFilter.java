package jcn.yourorder.authgateway.security.filter;

import jcn.yourorder.authgateway.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AuthenticationWebFilter {

    private final JwtTokenProvider jwtProvider;

    public JwtAuthenticationFilter(ReactiveAuthenticationManager authenticationManager, JwtTokenProvider jwtProvider) {
        super(authenticationManager);
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.web.server.WebFilterChain chain) {

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        if (!jwtProvider.validateToken(token)) {
            return chain.filter(exchange);
        }

        var claims = jwtProvider.getClaims(token);

        var auth = new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null,
                jwtProvider.getAuthorities(claims)
        );

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }
}
