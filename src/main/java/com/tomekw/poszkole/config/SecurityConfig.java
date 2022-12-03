package com.tomekw.poszkole.config;

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
                .mvcMatchers(HttpMethod.GET, "/teachers/{teacher-id}").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.DELETE, "/teachers/{teacher-id}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/teachers/{teacher-id}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/teachers/{teacher-id}/homeworks").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/teachers/{teacher-id}/lesson-groups").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/teachers/{teacher-id}/timetable").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)

                .mvcMatchers(HttpMethod.GET, "/parents").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.POST, "/parents").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{parent-id}").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/parents/{parent-id}").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/parents/{parent-id}").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{parent-id}/payments").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{parent-id}/students").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{parent-id}/students/{student-id}").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE)

                .mvcMatchers(HttpMethod.GET, "/students").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.POST, "/students").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/students/{student-id}").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.DELETE, "/students/{student-id}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/students/{student-id}").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/students/{student-id}/lessons").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/students/{student-id}/homeworks").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/students/{student-id}/parent").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/students/{student-id}/lesson-groups").hasAnyRole(ALL_FUNCTIONAL_ROLES)

                .mvcMatchers(HttpMethod.GET, "/payments").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.POST, "/payments").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/payments/{payment-id}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/payments/{payment-id}").hasRole(ADMIN_ROLE)

                .mvcMatchers(HttpMethod.GET, "/lesson-groups").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.POST, "/lesson-groups").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/lesson-groups/{lesson-group-id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/lesson-groups/{lesson-group-id}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/lesson-groups/{lesson-group-id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/lesson-groups/{lesson-group-id}/lessons").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/lesson-groups/{lesson-group-id}/teacher").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.GET, "/lesson-groups/{lesson-group-id}/students").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/lesson-groups/{lesson-group-id}/student-lesson-group-buckets/{student-lesson-group-bucket-id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/lesson-groups/{lesson-group-id}/student-lesson-group-buckets/{student-lesson-group-bucket-id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)

                .mvcMatchers(HttpMethod.GET, "/lessons").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.POST, "/lessons").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/lessons/{lesson-id}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/lessons/{lesson-id}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/lessons/{lesson-id}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/{lesson-id}/student-lesson-buckets/{student-lesson-bucket-id}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
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
