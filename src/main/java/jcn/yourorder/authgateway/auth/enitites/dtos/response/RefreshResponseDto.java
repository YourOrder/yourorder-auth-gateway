package jcn.yourorder.authgateway.auth.enitites.dtos.response;

public record RefreshResponseDto(
        TokenResponseDTO tokenResponseDTO,
        String message
) {
}
