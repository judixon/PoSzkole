package com.tomekw.poszkole;

import com.tomekw.poszkole.user.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.Calendar;

@SpringBootApplication
@EnableSwagger2
public class PoSzkoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoSzkoleApplication.class, args);

    }
}
