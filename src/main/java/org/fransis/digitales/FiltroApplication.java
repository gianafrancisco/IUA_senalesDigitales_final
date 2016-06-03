package org.fransis.digitales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.fransis.digitales")
public class FiltroApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiltroApplication.class, args);
	}
}
