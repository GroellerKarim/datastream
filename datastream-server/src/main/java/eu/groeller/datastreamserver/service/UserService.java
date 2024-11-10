package eu.groeller.datastreamserver.service;

import eu.groeller.datastreamserver.domain.User;
import eu.groeller.datastreamserver.persistence.user.UserRepository;
import eu.groeller.datastreamserver.presentation.request.user.UserLoginRequest;
import eu.groeller.datastreamserver.presentation.request.user.UserRequest;
import eu.groeller.datastreamserver.presentation.response.user.UserLoginResponse;
import eu.groeller.datastreamserver.service.security.JwtService;
import eu.groeller.datastreamserver.service.utils.DtoUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User createUser(UserRequest dto) {
        Objects.requireNonNull(dto, "UserRequest Body must not be null");
        DtoUtils.checkAllNullsAndBlanks(dto);

        if(userRepository.existsByEmail(dto.email())) 
            throw new IllegalArgumentException("Email " + dto.email() + " is already used by a different account.");
        if(userRepository.existsByUsername(dto.username())) 
            throw new IllegalArgumentException("Username " + dto.username() + " is already used by a different account.");

        var user = new User(dto.username(), dto.email(), dto.password(), passwordEncoder);
        return userRepository.save(user);
    }

    public UserLoginResponse login(UserLoginRequest dto) {
        Objects.requireNonNull(dto, "UserLoginRequest Body must not be null");
        DtoUtils.checkAllNullsAndBlanks(dto);

        // TODO CHANGE EXCEPTIONS
        val user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("No user with email [" + dto.email() + "] found!"));

        if (!user.comparePasswords(dto.password(), passwordEncoder)) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtService.generateToken(user);
        return new UserLoginResponse(user.getUsername(), user.getEmail(), token);
    }
}
