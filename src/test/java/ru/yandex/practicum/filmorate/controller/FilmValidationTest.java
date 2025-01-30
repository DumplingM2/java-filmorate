package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmValidationTest {

    @InjectMocks
    private FilmService filmService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidateFilm_NameIsBlank_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmService.validateFilm(film));

        assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    public void testValidateFilm_DescriptionTooLong_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("D".repeat(201));
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmService.validateFilm(film));

        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    public void testValidateFilm_ReleaseDateTooEarly_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(100);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmService.validateFilm(film));

        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    public void testValidateFilm_DurationNotPositive_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(0);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmService.validateFilm(film));

        assertEquals("Продолжительность фильма должна быть положительным числом", exception.getMessage());
    }
}
