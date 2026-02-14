package eu.groeller.ds.configuration;

import eu.groeller.ds.configuration.security.JwtConfig;
import eu.groeller.ds.configuration.security.PasswordConfig;
import eu.groeller.ds.configuration.security.SecurityConfig;
import eu.groeller.ds.service.security.CustomUserDetailsService;
import eu.groeller.ds.service.security.JwtService;
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
