package com.gofluent.backend.services;

import com.gofluent.backend.dtos.CredentialsDto;
import com.gofluent.backend.dtos.SignUpDto;
import com.gofluent.backend.dtos.UserDto;
import com.gofluent.backend.entities.Role;
import com.gofluent.backend.entities.User;
import com.gofluent.backend.exceptions.AppException;
import com.gofluent.backend.mappers.UserMapper;
import com.gofluent.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserDto login(CredentialsDto credentialsDto) {
        // Adaugă temporar în UserService.login()
        String testPassword = "admin123";
        String storedHash = "$2a$10$5MyBQ.VqIirTYvo6vf9xiePEHuLapXD07lkrT9xveE/RLLyZQFVLC";
        boolean testMatches = passwordEncoder.matches(testPassword, storedHash);
        System.out.println("🧪 Test password matches: " + testMatches);

        User user =  userRepository.findByLogin(credentialsDto.login()).orElseThrow(() -> new AppException("unknown user", HttpStatus.NOT_FOUND));



        if(passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(SignUpDto signUpDto) {
        Optional<User> oUser = userRepository.findByLogin(signUpDto.login());

        if(oUser.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }
        User user = userMapper.signUpToUser(signUpDto);

        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(signUpDto.password())));
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        System.out.println("🔍 Principal type: " + principal.getClass().getSimpleName());
        System.out.println("🔍 Principal: " + principal);

        if (principal instanceof UserDto) {
            UserDto userDto = (UserDto) principal;
            System.out.println("🔍 Searching for user with login: '" + userDto.getLogin() + "'");
            return userRepository.findByLogin(userDto.getLogin())
                    .orElseThrow(() -> new RuntimeException("User not found with login: " + userDto.getLogin()));
        } else {
            String username = principal.toString();
            System.out.println("🔍 Searching for user with username: '" + username + "'");
            return userRepository.findByLogin(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        }
    }
}
