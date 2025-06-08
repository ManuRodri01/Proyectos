package api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TpTacsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TpTacsApplication.class, args);
	}

}
