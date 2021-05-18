package ru.otus.example.facadegateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import ru.otus.example.facadegateway.security.UserDetailsServiceImp;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //@Autowired
    //private UserDetailsService userDetailsService;

    /*@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        inMemoryUserDetailsManager.createUser(new User("admin", passwordEncoder.encode("admin"), Collections.singletonList((GrantedAuthority) () -> "USER")));
        return inMemoryUserDetailsManager;
    }*/

    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImp();
    };

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http.csrf().disable().authorizeRequests().antMatchers("/").permitAll();

        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeRequests().antMatchers("/", "/favicon.ico").permitAll()
                .and()

                .authorizeRequests().antMatchers("/login-form-processing").anonymous()
                .and()

                .authorizeRequests().antMatchers("/**").authenticated()
                .and()

                .formLogin()
                .loginProcessingUrl("/login-form-processing")
                .usernameParameter("username-for-login")
                .passwordParameter("password-for-login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login-form")
                .and()

                .rememberMe()
                .key("secret")
                //.userDetailsService(userDetailsService())
                .alwaysRemember(true)
                .tokenValiditySeconds(6000)
        ;
    }

}
