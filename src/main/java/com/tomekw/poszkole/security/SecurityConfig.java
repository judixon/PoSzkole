package com.tomekw.poszkole.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
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

/*    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
        http.headers().frameOptions().disable();
        http.csrf().disable();
        return http.build();
    }*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests

                .mvcMatchers("/").permitAll()

                .mvcMatchers(HttpMethod.GET, "/teachers").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.POST, "/teachers").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/teachers/{id}").hasAnyRole(ALL_FUNCTIONAL_ROLES)
                .mvcMatchers(HttpMethod.DELETE, "/teachers/{id}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/teachers/{id}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/teachers/{id}/homeworks").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE) //sprawdzenie ID SecurityContextHolder czy Teacher pyta o siebie 2  **checkTeacherDetailedDataAcces OK
                .mvcMatchers(HttpMethod.GET, "/teachers/{id}/lessongroups").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE) //sprawdzenie ID SecurityContextHolder czy Teacher pyta o siebie 3 **checkTeacherDetailedDataAcces OK
                .mvcMatchers(HttpMethod.GET, "/teachers/{id}/timetable").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE) //sprawdzenie ID SecurityContextHolder czy Teacher pyta o siebie 4 **checkTeacherDetailedDataAcces OK

                .mvcMatchers(HttpMethod.GET, "/parents").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.POST, "/parents").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{id}").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE) //sprawdzenie dla czy Parent ma ID o które pyta 5 OK
                .mvcMatchers(HttpMethod.DELETE, "/parents/{id}").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/parents/{id}").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/parents/{id}/payments").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE) //sprawdzenie czy Parent ma to samo ID, o które pyta, ADMIN I TEACHER normalnie 6 m1 OK
                .mvcMatchers(HttpMethod.GET, "/parents/{id}/students").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE) //sprawdzenie czy Parent ma to samo ID, o które pyta, ADMIN I TEACHER normalnie 7 m1 OK
                .mvcMatchers(HttpMethod.GET, "/parents/{p_id}/students/{s_id}").hasAnyRole(PARENT_ROLE, ADMIN_ROLE, TEACHER_ROLE) //sprawdzenie czy Parent ma to samo ID, o które pyta, ADMIN I TEACHER normalnie 8 m1 OK

                .mvcMatchers(HttpMethod.GET, "/students").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.POST, "/students").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/students/{id}").hasAnyRole(ALL_FUNCTIONAL_ROLES) //STUDENT czy pyta o siebie, PARENT ma endpoint o swoich studsów OK
                .mvcMatchers(HttpMethod.DELETE, "/students/{id}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/students/{id}").hasAnyRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/students/{id}/lessons").hasAnyRole(ALL_FUNCTIONAL_ROLES) //STUDENT czy pyta o siebie, PARENT czy o swojego ucznia, TEACHER ADMIN normalnie 9 m1, OK
                .mvcMatchers(HttpMethod.GET, "/students/{id}/homeworks").hasAnyRole(ALL_FUNCTIONAL_ROLES) //STUDENT czy pyta o siebie, PARENT czy o swojego ucznia, TEACHER ADMIN normalnie 10 m1, OK
                .mvcMatchers(HttpMethod.GET, "/students/{id}/parent").hasAnyRole(ALL_FUNCTIONAL_ROLES)  //STUDENT czy pyta o siebie, PARENT czy o swojego ucznia, TEACHER ADMIN normalnie 11 m1, OK
                .mvcMatchers(HttpMethod.GET, "/students/{id}/groups").hasAnyRole(ALL_FUNCTIONAL_ROLES)  //STUDENT czy pyta o siebie, PARENT czy o swojego ucznia, TEACHER ADMIN normalnie 12 m1, OK

                .mvcMatchers(HttpMethod.GET, "/payments").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.POST, "/payments").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET, "/payments/{id}").hasAnyRole(TEACHER_ROLE, ADMIN_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/payments/{id}").hasRole(ADMIN_ROLE)

                .mvcMatchers(HttpMethod.GET, "/groups").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.POST, "/groups").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/groups/{id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/groups/{id}").hasRole(ADMIN_ROLE)
                .mvcMatchers(HttpMethod.PATCH, "/groups/{id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.GET, "/groups/{id}/lessons").hasAnyRole(ALL_FUNCTIONAL_ROLES) //PARENT I STUDENT tylko jesli student jest w grupie 15 OK
                .mvcMatchers(HttpMethod.GET, "/groups/{id}/teacher").hasAnyRole(ALL_FUNCTIONAL_ROLES) //PARENT I STUDENT tylko jesli student jest w grupie 16 OK
                .mvcMatchers(HttpMethod.GET, "/groups/{id}/students").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE)
                .mvcMatchers(HttpMethod.DELETE, "/groups/{g_id}/students/{s_id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE) //TEACHER tylko dla swojej grupy 17 OK
                .mvcMatchers(HttpMethod.PATCH, "/groups/{g_id}/students/{s_id}").hasAnyRole(ADMIN_ROLE, TEACHER_ROLE) //TEACHER tylko dla swojej grupy 18 OK

                .mvcMatchers(HttpMethod.GET,"/lessons").hasAnyRole(TEACHER_ROLE,ADMIN_ROLE)
                .mvcMatchers(HttpMethod.POST,"/lessons").hasAnyRole(TEACHER_ROLE,ADMIN_ROLE)
                .mvcMatchers(HttpMethod.GET,"/lessons/{id}").hasAnyRole(TEACHER_ROLE,ADMIN_ROLE) //TEACHER jeśli lekcja należy do jakiejś grupy teachera 19
                .mvcMatchers(HttpMethod.DELETE,"/lessons/{id}").hasAnyRole(TEACHER_ROLE,ADMIN_ROLE) //TEACHER jeśli lekcja należy do jakiejś grupy teachera 20
                .mvcMatchers(HttpMethod.PATCH,"/lessons/{id}").hasAnyRole(TEACHER_ROLE,ADMIN_ROLE) //TEACHER jeśli lekcja należy do jakiejś grupy teachera 21
                .mvcMatchers(HttpMethod.PATCH,"/lessons/{l_id}/students/{s_id}").hasAnyRole(TEACHER_ROLE,ADMIN_ROLE) //TEACHER jeśli lekcja należy do jakiejś grupy teachera 22
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
