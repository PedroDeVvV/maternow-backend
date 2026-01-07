package maternow.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDto(
        @NotBlank(message = "The name is required.")
        String name,
        @Email
        @NotBlank(message = "The e-mail is required.")
        String email,
        @NotBlank(message = "The password is required.")
        String password
) {
}
