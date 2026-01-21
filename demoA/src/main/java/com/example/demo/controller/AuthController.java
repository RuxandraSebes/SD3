package com.example.demo.controller;

import com.example.demo.dtos.RegisterRequest;
import com.example.demo.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String result = authService.register(request);

        if (result.startsWith("User registered successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return authService.login(username, password);
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam String token) {
        boolean valid = authService.validateToken(token);
        return valid ? "Token is valid" : "Invalid or expired token";
    }

    @DeleteMapping(value = "/{authUserId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("authUserId") Long authUserId) {
        authService.deleteUserByAuthUserId(authUserId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
