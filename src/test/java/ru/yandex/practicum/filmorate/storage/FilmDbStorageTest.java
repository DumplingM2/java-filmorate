package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmDbStorageTest.AdditionalConfig.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @TestConfiguration
    static class AdditionalConfig {
        @Bean
        public UserDbStorage userDbStorage(JdbcTemplate jdbcTemplate) {
            return new UserDbStorage(jdbcTemplate);
        }
    }

    @Test
    @DisplayName("Создаём фильм, а затем находим его по ID")
    void testCreateAndFindById() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Some description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        // Устанавливаем MPA (предположим, что в data.sql есть запись с id=1, name='G')
        MpaRating mpa = new MpaRating();
        mpa.setId(1L);
        mpa.setName("G");
        film.setMpa(mpa);

        // Устанавливаем жанры (если в data.sql есть записи, например: id=1 - "Комедия", id=2 - "Драма")
        Genre comedy = new Genre();
        comedy.setId(1L);
        comedy.setName("Комедия");
        Genre drama = new Genre();
        drama.setId(2L);
        drama.setName("Драма");
        film.setGenres(Set.of(comedy, drama));

        Film created = filmStorage.create(film);
        assertThat(created.getId()).isNotNull().isPositive();

        Film found = filmStorage.findById(created.getId());
        assertThat(found.getName()).isEqualTo("Test Film");
        assertThat(found.getMpa().getId()).isEqualTo(1L);
        assertThat(found.getGenres()).hasSize(2);
    }

    @Test
    @DisplayName("Проверяем метод findAll()")
    void testFindAll() {
        // Изначально база должна быть пуста (если data.sql не создаёт фильмов)
        Collection<Film> emptyList = filmStorage.findAll();
        assertThat(emptyList).isEmpty();

        // Создадим один фильм
        Film film = new Film();
        film.setName("Film 1");
        film.setDescription("Desc 1");
        film.setReleaseDate(LocalDate.of(1999, 12, 12));
        film.setDuration(90);

        MpaRating mpa = new MpaRating();
        mpa.setId(2L);
        mpa.setName("PG");
        film.setMpa(mpa);

        filmStorage.create(film);

        // Теперь findAll() должен вернуть 1 фильм
        Collection<Film> films = filmStorage.findAll();
        assertThat(films).hasSize(1);
    }

    @Test
    @DisplayName("Проверяем обновление фильма")
    void testUpdate() {
        // Сначала создаём фильм
        Film film = new Film();
        film.setName("Old Film");
        film.setDescription("Old desc");
        film.setReleaseDate(LocalDate.of(1980, 5, 5));
        film.setDuration(100);

        MpaRating mpa = new MpaRating();
        mpa.setId(3L); // например, PG-13
        mpa.setName("PG-13");
        film.setMpa(mpa);

        Film created = filmStorage.create(film);

        // Обновляем данные фильма
        created.setName("New Film");
        created.setDescription("New desc");
        created.setDuration(110);

        MpaRating newMpa = new MpaRating();
        newMpa.setId(4L); // например, R
        newMpa.setName("R");
        created.setMpa(newMpa);

        filmStorage.update(created);

        // Проверяем, что обновление прошло успешно
        Film updated = filmStorage.findById(created.getId());
        assertThat(updated.getName()).isEqualTo("New Film");
        assertThat(updated.getMpa().getId()).isEqualTo(4L);
        assertThat(updated.getMpa().getName()).isEqualTo("R");
    }

    @Test
    @DisplayName("Проверяем удаление фильма")
    void testDelete() {
        Film film = new Film();
        film.setName("ToDelete");
        film.setDescription("Will be deleted");
        film.setReleaseDate(LocalDate.of(2005, 1, 1));
        film.setDuration(99);

        MpaRating mpa = new MpaRating();
        mpa.setId(1L);
        mpa.setName("G");
        film.setMpa(mpa);

        film = filmStorage.create(film);

        filmStorage.delete(film.getId());

        Film finalFilm = film;
        assertThatThrownBy(() -> filmStorage.findById(finalFilm.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Проверяем добавление и удаление лайка")
    void testLikes() {
        // Создаем пользователя, который будет ставить лайк
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user1");
        user.setName("User One");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user = userStorage.create(user);

        // Создаем фильм
        Film film = new Film();
        film.setName("Liked Film");
        film.setDescription("Film for testing likes");
        film.setReleaseDate(LocalDate.of(2010, 10, 10));
        film.setDuration(100);

        MpaRating mpa = new MpaRating();
        mpa.setId(2L);
        mpa.setName("PG");
        film.setMpa(mpa);

        film = filmStorage.create(film);

        // Добавляем лайк от созданного пользователя
        filmStorage.addLike(film.getId(), user.getId());

        // Проверяем, что фильм появился в топе с лайком
        Collection<Film> popular = filmStorage.getPopularFilms(10);
        assertThat(popular).hasSize(1);
        Film popularFilm = popular.iterator().next();
        assertThat(popularFilm.getId()).isEqualTo(film.getId());

        // Удаляем лайк
        filmStorage.removeLike(film.getId(), user.getId());
    }
}
