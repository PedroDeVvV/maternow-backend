package maternow.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordDto(
        @NotBlank(message = "Invalid password or email")
        String login,
        int type) {
}
