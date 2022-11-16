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
                .mvcMatchers(HttpMethod.GET, "/teachers/{lessonId}").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.DELETE, "/teachers/{lessonId}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/teachers/{lessonId}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/teachers/{lessonId}/homeworks").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/teachers/{lessonId}/lessongroups").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/teachers/{lessonId}/timetable").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)

                .mvcMatchers(HttpMethod.GET, "/parents").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.POST, "/parents").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{lessonId}").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/parents/{lessonId}").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/parents/{lessonId}").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{lessonId}/payments").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{lessonId}/students").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{p_id}/students/{s_id}").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE)

                .mvcMatchers(HttpMethod.GET, "/students").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.POST, "/students").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/students/{lessonId}").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.DELETE, "/students/{lessonId}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/students/{lessonId}").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/students/{lessonId}/lessons").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/students/{lessonId}/homeworks").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/students/{lessonId}/parent").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/students/{lessonId}/groups").hasAnyRole(ALL_FUNCTIONAL_ROLES)

                .mvcMatchers(HttpMethod.GET, "/payments").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.POST, "/payments").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/payments/{lessonId}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/payments/{lessonId}").hasRole(ADMIN_ROLE)

                .mvcMatchers(HttpMethod.GET, "/groups").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.POST, "/groups").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/groups/{lessonId}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/groups/{lessonId}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/groups/{lessonId}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/groups/{lessonId}/lessons").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/groups/{lessonId}/teacher").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/groups/{lessonId}/students").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/groups/{g_id}/students/{s_id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/groups/{g_id}/students/{s_id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)

                .mvcMatchers(HttpMethod.GET, "/lessons").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.POST, "/lessons").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/lessons/{lessonId}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/lessons/{lessonId}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/lessons/{lessonId}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
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
