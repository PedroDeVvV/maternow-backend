package maternow.infra.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public ResponseEntity<String> UserRegister() {
        return ResponseEntity.ok("Hello World");
    }
}
