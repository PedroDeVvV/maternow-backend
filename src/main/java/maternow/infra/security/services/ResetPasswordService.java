package maternow.infra.security.services;

import maternow.domain.repository.ResetPasswordRepository;
import maternow.domain.repository.UserRepository;
import maternow.domain.resetPassword.ResetPassword;
import maternow.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResetPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    public String generateHash(String login, int type) {

        Optional<User> userOptional;

        if (type == 0) {
            userOptional = userRepository.findByEmail(login);
        } else if (type == 1) {
            userOptional = userRepository.findByPhone(login);
        } else {
            throw new IllegalArgumentException("Invalid type");
        }

        User user = userOptional.orElseThrow(() -> new RuntimeException("User not found"));

        String rawToken = UUID.randomUUID().toString();

        String tokenHash = sha256(rawToken);

        ResetPassword resetToken = new ResetPassword();
        resetToken.setUser(user);
        resetToken.setTokenHash(tokenHash);
        resetToken.setCreatedAt(LocalDateTime.now());
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        resetToken.setUsed(false);

        resetPasswordRepository.save(resetToken);

        return rawToken;
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }

    }

}
