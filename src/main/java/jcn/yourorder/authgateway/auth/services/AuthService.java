package jcn.yourorder.authgateway.auth.services;

import jcn.yourorder.authgateway.auth.enitites.dtos.request.LoginRequestDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.request.RefreshRequestDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.request.RegisterRequestDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.response.LoginResponseDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.response.RefreshResponseDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.response.RegisterResponseDto;
import jcn.yourorder.authgateway.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Mono<RegisterResponseDto> registerUser(RegisterRequestDto request) {

        return Mono.empty();
    }

    public Mono<LoginResponseDto> loginUser(LoginRequestDto request) {
        return Mono.empty();
    }

    public Mono<RefreshResponseDto> refreshUserToken(RefreshRequestDto request) {
        return Mono.empty();
    }
}