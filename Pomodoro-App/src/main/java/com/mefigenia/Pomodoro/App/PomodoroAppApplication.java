package com.mefigenia.Pomodoro.App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PomodoroAppApplication {

	public static void main(String[] args) {
		PomodoroWatch watch = new PomodoroWatch();
		SpringApplication.run(PomodoroAppApplication.class, args);

	}

}
