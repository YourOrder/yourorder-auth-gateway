package jcn.yourorder.authgateway.security.filter;

import jcn.yourorder.authgateway.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtTokenProvider jwtProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             WebFilterChain chain) {

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