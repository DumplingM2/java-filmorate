package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository("genreDbStorage")
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Получить список всех жанров.
     */
    public List<Genre> findAll() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    /**
     * Получить жанр по идентификатору.
     */
    public Optional<Genre> findById(Long id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        List<Genre> list = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    /**
     * Пример, если хотите добавить метод create (опционально).
     */
    public Genre create(Genre genre) {
        String sql = "INSERT INTO genres (name) VALUES (?)";
        jdbcTemplate.update(sql, genre.getName());
        // Если нужен автоинкремент — можно воспользоваться KeyHolder, как в UserDbStorage
        return genre;
    }

    /**
     * Преобразование ResultSet в объект Genre.
     */
    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre g = new Genre();
        g.setId(rs.getLong("id"));
        g.setName(rs.getString("name"));
        return g;
    }
}
