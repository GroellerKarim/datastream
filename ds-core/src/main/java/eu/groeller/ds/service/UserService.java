package eu.groeller.ds.service;

import eu.groeller.ds.domain.User;
import eu.groeller.ds.persistence.user.UserRepository;
import eu.groeller.ds.presentation.request.user.UserLoginRequest;
import eu.groeller.ds.presentation.request.user.UserRequest;
import eu.groeller.ds.presentation.response.user.UserLoginResponse;
import eu.groeller.ds.service.security.JwtService;
import eu.groeller.ds.service.utils.DtoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User createUser(UserRequest dto) {
        if (dto == null) {
            log.warn("UserRequest Dto was null");
            throw new NullPointerException("UserRequest Body must not be null");
        }
        DtoUtils.checkAllNullsAndBlanks(dto);

        if (userRepository.existsByEmail(dto.email())) {
            log.warn("Email [{}] is already used by a different account", dto.email());
            throw new IllegalArgumentException("Email " + dto.email() + " is already used by a different account.");
        }
        if (userRepository.existsByUsername(dto.username())) {
            log.warn("Username [{}] is already used by a different account", dto.username());
            throw new IllegalArgumentException("Username " + dto.username() + " is already used by a different account.");
        }

        var user = userRepository.save(new User(dto.username(), dto.email(), dto.password(), passwordEncoder));
        log.info("Created User [{}]", user.getUsername());
        return user;
    }

    public UserLoginResponse login(UserLoginRequest dto) {
        if (dto == null) {
            log.warn("UserLoginRequest Dto was null");
            throw new NullPointerException("UserLoginRequest Body must not be null");
        }
        DtoUtils.checkAllNullsAndBlanks(dto);

        // TODO CHANGE EXCEPTIONS
        val user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> {
                    log.warn("No user with email [{}] found!", dto.email());
                    return new IllegalArgumentException("No user with email [" + dto.email() + "] found!");
                });

        log.debug("Comparing passwords");
        if (!user.comparePasswords(dto.password(), passwordEncoder)) {
            log.warn("Invalid password provided for user [{}]", user.getUsername());
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtService.generateToken(user);
        log.info("User Login accept, token generated, building Dto");
        return new UserLoginResponse(user.getUsername(), user.getEmail(), token);
    }
}
