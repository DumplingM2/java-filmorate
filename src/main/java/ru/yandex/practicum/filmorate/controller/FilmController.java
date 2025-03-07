package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.HashSet;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Request to get all films");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Request to create film: {}", film);
        // Если genres отсутствует, устанавливаем пустой набор:
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
        // Если mpa отсутствует, устанавливаем значение по умолчанию (id=1, name="G")
        if (film.getMpa() == null) {
            MpaRating defaultMpa = new MpaRating();
            defaultMpa.setId(1L);
            defaultMpa.setName("G");
            film.setMpa(defaultMpa);
        }
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Request to update film: {}", film);
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
        if (film.getMpa() == null) {
            MpaRating defaultMpa = new MpaRating();
            defaultMpa.setId(1L);
            defaultMpa.setName("G");
            film.setMpa(defaultMpa);
        }
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Request to get film by id: {}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Request to add like: film id = {}, user id = {}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Request to remove like: film id = {}, user id = {}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Request to get popular films with count: {}", count);
        return filmService.getPopularFilms(count);
    }
}
