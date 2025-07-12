package essay.essay.controllers;

import essay.essay.Models.AuthProvider;
import essay.essay.Models.Role;
import essay.essay.Models.UserModel;
import essay.essay.repository.UserRepo;
import essay.essay.security.JwtService;
import essay.essay.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("http://localhost:3000/")
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    UserRepo userRepo;

    @PostMapping("/auth/google-register")
    public ResponseEntity<Map<String, String>> registerGoogleUser(@RequestBody UserModel user) {
        // Check if user exists, create if needed
        Optional<UserModel> existingUser = userService.findUser(user.getEmail());
        if (existingUser.isEmpty()) {
            user.setCreateAt(new Date(System.currentTimeMillis()));
            user.setRole(Role.CLIENT); // default role
            user.setAuthProvider(AuthProvider.GOOGLE); // form-based registration
            userService.saveUser(user);
        }

        // Generate JWT
        String token = jwtService.generateToken(user.getEmail(), String.valueOf(user.getRole()));

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> userRegister(@RequestBody UserModel userdetails) {
        Map<String, String> response = new HashMap<>();

        try {
            boolean exists = userService.findUser(userdetails.getEmail()).isPresent();

            if (exists) {
                response.put("message", "Email already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            userdetails.setCreateAt(new Date(System.currentTimeMillis()));
            userdetails.setRole(Role.CLIENT); // default role
            userdetails.setAuthProvider(AuthProvider.FORM); // form-based registration
            userService.saveUser(userdetails);

            String token = jwtService.generateToken(userdetails.getEmail(), userdetails.getRole().name());

            response.put("message", "Registration is okay");
            response.put("token", token); // 🔐 return token

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "An error occurred during registration");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/users")
    public List<UserModel> getAllUsers(){

        return userRepo.findAll();
    }
}
