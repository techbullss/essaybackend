package essay.essay.controllers;

import essay.essay.Models.LoginRequest;
import essay.essay.Models.UserModel;

import essay.essay.repository.UserRepo;
import essay.essay.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        UserModel user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));;

        Map<String, String> response = new HashMap<>();

        if (user == null || !user.getPassword().equals(request.getPassword())) {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        response.put("message", "Login successful");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}

