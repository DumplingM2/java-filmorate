package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       MpaDbStorage mpaDbStorage,
                       GenreDbStorage genreDbStorage,
                       UserService userService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.userService = userService;
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
        checkFilmExists(film.getId());
        return filmStorage.update(film);
    }

    public Film getFilmById(Long id) {
        checkFilmExists(id);
        return filmStorage.findById(id);
    }

    public boolean addLike(Long filmId, Long userId) {
        checkFilmExists(filmId);
        userService.validateUserExists(userId);
        return filmStorage.addLike(filmId, userId);
    }

    public boolean removeLike(Long filmId, Long userId) {
        checkFilmExists(filmId);
        userService.validateUserExists(userId);
        return filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public void deleteFilm(Long id) {
        checkFilmExists(id);
        filmStorage.delete(id);
    }

    private void checkFilmExists(Long filmId) {
        if (filmId == null || filmStorage.findById(filmId) == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    public void validateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Фильм не может быть null");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза не может быть null");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ValidationException("Рейтинг MPA не может быть null");
        }
        // Проверяем, что такой MPA реально есть в БД
        mpaDbStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("MPA рейтинг не найден с id=" + film.getMpa().getId()));

        if (film.getGenres() == null) {
            throw new ValidationException("Жанры не могут быть null");
        }
        List<Long> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .distinct()
                .collect(Collectors.toList());
        List<Genre> existingGenres = genreDbStorage.findByIds(genreIds);
        if (existingGenres.size() != genreIds.size()) {
            // Определяем отсутствующий id
            Set<Long> existingIds = existingGenres.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            for (Long id : genreIds) {
                if (!existingIds.contains(id)) {
                    throw new NotFoundException("Жанр не найден с id=" + id);
                }
            }
        }
    }
}
