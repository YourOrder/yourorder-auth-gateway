package jcn.yourorder.authgateway.security.filter;

import io.jsonwebtoken.Claims;
import jcn.yourorder.authgateway.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
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

        Claims claims = jwtProvider.getClaims(token);

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-User-Id", claims.getSubject())
                .header("X-Username", claims.get("username", String.class))
                .header("X-User-Role", claims.get("role", String.class))
                .header("X-Tenant-Id", claims.get("tenantId", String.class))
                .build();

        ServerWebExchange newExchange = exchange.mutate()
                .request(request)
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                claims.getSubject(),
                null,
                jwtProvider.getAuthorities(claims)
        );

        return chain.filter(newExchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }
}