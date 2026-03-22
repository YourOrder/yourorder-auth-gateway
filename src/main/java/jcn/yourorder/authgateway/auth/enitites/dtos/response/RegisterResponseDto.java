package jcn.yourorder.authgateway.auth.enitites.dtos.response;

public record RegisterResponseDto(
        TokenResponseDTO tokenResponseDTO,
        String message
) {}
