package eu.groeller.datastreamserver.configuration;

import eu.groeller.datastreamserver.configuration.security.JwtConfig;
import eu.groeller.datastreamserver.configuration.security.PasswordConfig;
import eu.groeller.datastreamserver.configuration.security.SecurityConfig;
import eu.groeller.datastreamserver.service.security.CustomUserDetailsService;
import eu.groeller.datastreamserver.service.security.JwtService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebMvcTest
@Import({
    SecurityConfig.class,
    JwtService.class,
    JwtConfig.class,
    CustomUserDetailsService.class,
    PasswordConfig.class
})
public @interface SecuredWebMvcTest {
    @AliasFor(annotation = WebMvcTest.class, attribute = "value")
    Class<?>[] value() default {};
}
