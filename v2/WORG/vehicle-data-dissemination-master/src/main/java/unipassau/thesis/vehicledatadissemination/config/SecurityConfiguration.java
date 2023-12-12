package unipassau.thesis.vehicledatadissemination.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * This section defines the user accounts which can be used for
     * authentication as well as the roles each user has.
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("alice").password(passwordEncoder().encode("alice")).roles("data_owner").and()
                .withUser("bob").password(passwordEncoder().encode("bob")).roles("data_consumer").and()
                .withUser("carlos").password(passwordEncoder().encode("carlos")).roles("data_consumer");
    }

    /**
     * This section defines the security policy for the app.
     * - BASIC authentication is supported (enough for this REST-based demo)

     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .httpBasic().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/authorize").hasRole("data_consumer").and()
                .csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
/*
1.@Configuration Annotation:
-This class is marked with @Configuration, indicating that it contains configuration for the application.

2.@EnableGlobalMethodSecurity(prePostEnabled = true) Annotation:
-This annotation enables Spring Security's global method security. It allows the use of @PreAuthorize, @PostAuthorize, and related
  annotations for method-level security.

3.Extending WebSecurityConfigurerAdapter:
-The class extends WebSecurityConfigurerAdapter, which is a convenient base class for creating a custom security configuration.

4. configure(AuthenticationManagerBuilder auth) Method:
-This method is used to configure authentication in memory. It defines user accounts (alice, bob, and carlos) along with their encoded
  passwords and roles (data_owner and data_consumer).
-The passwords are encoded using the BCryptPasswordEncoder.

5.configure(HttpSecurity http) Method:
-This method configures the security policy for the application. It specifies that HTTP Basic authentication is used and defines
  authorization rules for different endpoints.
-Specifically, it allows only users with the role data_consumer to access the /authorize endpoint using the POST method. Other requests
  are denied.
-CSRF (Cross-Site Request Forgery) protection is disabled in this configuration.

6.@Bean-annotated passwordEncoder Method:
-This method creates and returns an instance of BCryptPasswordEncoder, which is a password encoder used to securely store and
 validate passwords.

**Summary**

This security configuration class is setting up in-memory authentication with specific user roles, defining authorization rules for
different endpoints, and specifying a password encoder. It's a basic security configuration allowing access to certain endpoints only
for users with specific roles and disabling CSRF protection. The passwords are securely stored using BCrypt encoding. The use of
@EnableGlobalMethodSecurity enables method-level security annotations for more fine-grained access control in the application.


 */