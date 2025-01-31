package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Map<Long, Set<Long>> filmLikes = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(idGenerator.incrementAndGet());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        filmLikes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        return true;
    }

    @Override
    public boolean removeLike(Long filmId, Long userId) {
        filmLikes.computeIfAbsent(filmId, k -> new HashSet<>()).remove(userId);
        return true;
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> filmLikes.getOrDefault(f2.getId(), new HashSet<>()).size() - filmLikes.getOrDefault(f1.getId(), new HashSet<>()).size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
