package com.gofluent.backend.controllers;

import com.gofluent.backend.config.UserAuthProvider;
import com.gofluent.backend.entities.User;
import com.gofluent.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserAuthProvider userAuthProvider;


    @GetMapping("/current-level")
    public Long getCurrentLevelId(@RequestHeader("Authorization") String token) {
        String username = userAuthProvider.getUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (user.getCurrentLevel() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has no level assigned");
        }
        return user.getCurrentLevel().getId();
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        String username = userAuthProvider.getUsername(token.replace("Bearer ", ""));
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Returnează datele utilizatorului
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("firstName", user.getFirstName());
        userInfo.put("lastName", user.getLastName());
        userInfo.put("login", user.getLogin());
        userInfo.put("role", user.getRole());

        return ResponseEntity.ok(userInfo);
    }
}
