package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film);
        if (film.getId() == null || filmStorage.getFilmById(film.getId()) == null) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        return filmStorage.update(film);
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return film;
    }

    public boolean addLike(Long filmId, Long userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return filmStorage.addLike(filmId, userId);
    }

    public boolean removeLike(Long filmId, Long userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> popularFilms = filmStorage.getPopularFilms(count);
        if (popularFilms.isEmpty()) {
            throw new NotFoundException("Популярные фильмы не найдены");
        }
        return popularFilms;
    }

    public void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        if (film.getGenres() == null) {
            throw new ValidationException("Жанры не могут быть null");
        }
        if (film.getMpa() == null) {
            throw new ValidationException("Рейтинг MPA не может быть null");
        }
    }
}
