package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FilmorateApplicationTests {

	@Autowired
	private FilmController filmController;

	@Autowired
	private UserController userController;

	@Autowired
	private FilmService filmService;

	@Autowired
	private UserService userService;

	@Test
	void contextLoads() {
		// Проверяем, что бины действительно поднимаются в контексте
		assertThat(filmController).isNotNull();
		assertThat(userController).isNotNull();
		assertThat(filmService).isNotNull();
		assertThat(userService).isNotNull();
	}
}
