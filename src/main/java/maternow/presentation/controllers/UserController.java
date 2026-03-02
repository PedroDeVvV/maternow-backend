package maternow.presentation.controllers;

import jakarta.validation.Valid;
import maternow.domain.repository.UserRepository;
import maternow.domain.user.User;
import maternow.infra.security.services.ResetPasswordService;
import maternow.infra.security.services.TokenService;
import maternow.presentation.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ResetPasswordService resetPasswordService;

    @Autowired
    TokenService tokenService;

    @Autowired
    private UserRepository repository;

    @PostMapping("/login")
    public ResponseEntity UserAuthentication(@RequestBody @Valid AuthenticatorDTO data) {

        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(new LoginTokenDTO(token));
        } catch (AuthenticationException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid user or password");
        }

    }

    @PostMapping("/register")
    public ResponseEntity UserRegister(@RequestBody @Valid RegisterDto data) {
        if (this.repository.findByEmail(data.email()) != null)
            return ResponseEntity.badRequest().body("This e-mail is already registered.");
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.name(), data.email(), encryptedPassword, data.phone());

        this.repository.save(newUser);

        return ResponseEntity.ok(new UserSummary(newUser.getId(), newUser.getName(), newUser.getUsername()));
    }

    @PostMapping("/generateHash")
    public ResponseEntity GenerateHash(@RequestBody @Valid ResetPasswordDto data) {

        String hashPassword = resetPasswordService.generateHash(data.login(), data.type());

        return ResponseEntity.ok(new hashResetToken(hashPassword));
    }

}
