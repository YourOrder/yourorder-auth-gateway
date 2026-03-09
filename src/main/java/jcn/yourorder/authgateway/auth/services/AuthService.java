package jcn.yourorder.authgateway.auth.services;

import jcn.yourorder.authgateway.auth.enitites.dtos.request.LoginRequestDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.request.RefreshRequestDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.request.RegisterRequestDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.response.LoginResponseDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.response.RefreshResponseDto;
import jcn.yourorder.authgateway.auth.enitites.dtos.response.RegisterResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final TokenService tokenService;

    public Mono<RegisterResponseDto> registerUser(RegisterRequestDto request) {
        return userService.register(request)
                .flatMap(tokenService::generateTokens)
                .map(tokens ->
                        new RegisterResponseDto(
                                tokens,
                                "User successfully registered"
                        )
                );
    }

    public Mono<LoginResponseDto> loginUser(LoginRequestDto request) {
        return userService.login(request)
                .flatMap(tokenService::generateTokens)
                .map(tokens ->
                        new LoginResponseDto(
                                tokens,
                                "User successfully authenticated"
                        )
                );
    }

    public Mono<RefreshResponseDto> refreshUserToken(RefreshRequestDto request) {
        return tokenService.refreshAccessToken(request.refreshToken())
                .map(tokens ->
                        new RefreshResponseDto(
                                tokens,
                                "Access token successfully refreshed"
                        )
                );
    }

    public Mono<Void> logoutUser(RefreshRequestDto request) {
        return tokenService.logout(request.refreshToken());
    }
}