package com.example.demo.service;

import com.example.demo.dtos.PersonAuthRequestDTO;
import com.example.demo.dtos.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.rabbitmq.RabbitMqMessage;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final RabbitMqMessage rabbitMqMessage;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    @Value("${microservice.people.url}")
    private String peopleServiceUrl;

    public AuthService(UserRepository userRepository, RabbitMqMessage rabbitMqMessage, JwtUtil jwtUtil, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.rabbitMqMessage = rabbitMqMessage;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
    }

    public String register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "User already exists!";
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = new User(request.getUsername(), encodedPassword, request.getRole());
        userRepository.save(newUser);

        try {
            PersonAuthRequestDTO peopleRequest = new PersonAuthRequestDTO(
                    newUser.getId(),
                    request.getName(),
                    request.getAge(),
                    request.getAddress()
            );


            rabbitMqMessage.sendToPeopleQueue(peopleRequest);
            return "User registered successfully in Auth and People Service!";


        } catch (ResourceAccessException e) {
            return "User registered, but People Service is unreachable (Check Docker network/URL): " + e.getMessage();
        } catch (Exception e) {
            return "User registered, but error linking to People Service: " + e.getMessage();
        }

    }


    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return "User not found!";
        }

        User user = userOpt.get();
        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
        } else {
            return "Invalid password!";
        }
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Transactional
    public void deleteUserByAuthUserId(Long authUserId) {
        Optional<User> userOptional = userRepository.findById(authUserId);

        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
        }
    }
}