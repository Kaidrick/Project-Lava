package moe.ofs.backend.http.config;

import lombok.RequiredArgsConstructor;
import moe.ofs.backend.http.security.filter.PasswordTypeFilter;
import moe.ofs.backend.http.security.handler.FailureHandler;
import moe.ofs.backend.http.security.handler.SuccessHandler;
import moe.ofs.backend.http.security.provider.PasswordTypeProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
@RequiredArgsConstructor
@Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private final SuccessHandler successHandler;
    private final FailureHandler failureHandler;
    private final PasswordTypeProvider passwordTypeProvider;

    public PasswordTypeFilter passwordTypeFilter() throws Exception {
        PasswordTypeFilter passwordTypeFilter = new PasswordTypeFilter();
        passwordTypeFilter.setAuthenticationManager(super.authenticationManagerBean());
        passwordTypeFilter.setAuthenticationFailureHandler(failureHandler);
        passwordTypeFilter.setAuthenticationSuccessHandler(successHandler);
        return passwordTypeFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();

        http
                .addFilterBefore(passwordTypeFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
//                所有请求放行
//                .antMatchers("/h2-console/**")
                .anyRequest()
                .permitAll()
                .and()
                .cors().and()
                .csrf().disable()
        ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(passwordTypeProvider);
    }

}