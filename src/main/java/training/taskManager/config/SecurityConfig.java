package training.taskManager.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import training.taskManager.model.User;
import training.taskManager.model.Workspace;
import training.taskManager.service.UserService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig  {
    @Autowired
    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authz) -> {
                        try {
                            authz
                                    .mvcMatchers("/").permitAll()
                                    .anyRequest().authenticated()
                                    .and()
                                    .csrf().disable();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

            )
            .logout(logout -> logout
                    .logoutSuccessUrl("/").permitAll())
            .oauth2Login(oauth -> oauth
                    .defaultSuccessUrl("/start", true)
                    .userInfoEndpoint(userInfo -> userInfo
                            .oidcUserService(this.oidcUserService())
                    )
            );

        return http.build();
    }


    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);

            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            Map<String, Object> data = oidcUser.getAttributes();

            User user = userService.findUserByEmail((String) data.get("email"));

            if (user == null) {
                userService.addUser(data);
            }

            oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());

            return oidcUser;
        };
    }
}

