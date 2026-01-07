package maternow.presentation.controllers;

import jakarta.validation.Valid;
import maternow.domain.repository.UserRepository;
import maternow.domain.user.User;
import maternow.presentation.dto.AuthenticatorDTO;
import maternow.presentation.dto.RegisterDto;
import maternow.presentation.dto.UserSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private UserRepository repository;

    @PostMapping("/login")
    public ResponseEntity UserAuthentication(@RequestBody @Valid AuthenticatorDTO data) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity UserRegister(@RequestBody @Valid RegisterDto data) {
        if (this.repository.findByEmail(data.email()) != null)
            return ResponseEntity.badRequest().body("This e-mail is already registered.");
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.name(), data.email(), encryptedPassword);

        this.repository.save(newUser);

        return ResponseEntity.ok(new UserSummary(newUser.getId(), newUser.getName(), newUser.getUsername()));
    }
}
