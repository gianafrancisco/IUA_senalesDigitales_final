package org.fransis.digitales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

@SpringBootApplication
@ComponentScan("org.fransis.digitales")
public class FiltroApplication {

	public static void main(String[] args) {

		Mixer.Info[] info = AudioSystem.getMixerInfo();
		int m = 0;
		for(Mixer.Info i: info){
			System.out.println(m+" "+i.getDescription());
			m++;
		}

		SpringApplication.run(FiltroApplication.class, args);
	}
}
