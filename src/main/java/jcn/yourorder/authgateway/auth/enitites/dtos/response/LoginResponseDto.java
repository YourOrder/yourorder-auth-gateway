package jcn.yourorder.authgateway.auth.enitites.dtos.response;

public record LoginResponseDto(
        TokenResponseDTO tokenResponseDTO,
        String message
) {}
