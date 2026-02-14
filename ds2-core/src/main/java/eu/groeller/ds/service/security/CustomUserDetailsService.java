package eu.groeller.ds.service.security;

import eu.groeller.ds.configuration.security.CustomUserDetails;
import eu.groeller.ds.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        log.debug("Searching for User with id [{}]", id);
        var user = userRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> {
                    log.warn("Did not find user with id [{}]!", id);
                    return new UsernameNotFoundException("User with id [" + id + "] not found");
                });
        log.debug("Found user [{}]", user.getUsername());
        return new CustomUserDetails(user);
    }
}
