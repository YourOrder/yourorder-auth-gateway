package jcn.yourorder.authgateway.auth.enitites.dtos.response;

public record TokenResponseDTO(
        String accessToken,
        String refreshToken
) {}
