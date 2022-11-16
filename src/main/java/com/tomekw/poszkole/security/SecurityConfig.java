package com.tomekw.poszkole.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String STUDENT_ROLE = "STUDENT";
    private static final String PARENT_ROLE = "PARENT";
    private static final String TEACHER_ROLE = "TEACHER";
    private static final String[] ALL_FUNCTIONAL_ROLES = {ADMIN_ROLE, STUDENT_ROLE, PARENT_ROLE, TEACHER_ROLE};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests

                .mvcMatchers("/").permitAll()

                .mvcMatchers(HttpMethod.GET, "/teachers").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.POST, "/teachers").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/teachers/{id}").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.DELETE, "/teachers/{id}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/teachers/{id}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/teachers/{id}/homeworks").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/teachers/{id}/lessongroups").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/teachers/{id}/timetable").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)

                .mvcMatchers(HttpMethod.GET, "/parents").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.POST, "/parents").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{id}").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/parents/{id}").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/parents/{id}").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{id}/payments").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{id}/students").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{p_id}/students/{s_id}").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE)

                .mvcMatchers(HttpMethod.GET, "/students").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.POST, "/students").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/students/{id}").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.DELETE, "/students/{id}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/students/{id}").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/students/{id}/lessons").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/students/{id}/homeworks").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/students/{id}/parent").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/students/{id}/groups").hasAnyRole(ALL_FUNCTIONAL_ROLES)

                .mvcMatchers(HttpMethod.GET, "/payments").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.POST, "/payments").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/payments/{id}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/payments/{id}").hasRole(ADMIN_ROLE)

                .mvcMatchers(HttpMethod.GET, "/groups").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.POST, "/groups").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/groups/{id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/groups/{id}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/groups/{id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/groups/{id}/lessons").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/groups/{id}/teacher").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/groups/{id}/students").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/groups/{g_id}/students/{s_id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/groups/{g_id}/students/{s_id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)

                .mvcMatchers(HttpMethod.GET, "/lessons").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.POST, "/lessons").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/lessons/{id}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/lessons/{id}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/lessons/{id}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/lessons/{l_id}/students/{s_id}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
        );
        http.formLogin();
        http.httpBasic();
        http.headers().frameOptions().disable();
        http.csrf().disable();
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
