package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase // Автоматически подменяет DataSource на тестовую (in-memory) H2
@Import(UserDbStorage.class) // Подключаем конкретный класс DAO, который будем тестировать
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    @DisplayName("Проверяем, что после создания пользователя можно найти его по ID")
    void testCreateAndFindById() {
        // Создаём пользователя
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        // Сохраняем в базе
        User created = userStorage.create(user);
        assertThat(created.getId()).isNotNull().isPositive();

        // Ищем по ID
        User found = userStorage.findById(created.getId());
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@example.com");
        assertThat(found.getLogin()).isEqualTo("testlogin");
        assertThat(found.getName()).isEqualTo("Test Name");
        assertThat(found.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("Проверяем, что метод findAll() возвращает всех пользователей")
    void testFindAll() {
        // Сначала база пуста, проверим
        Collection<User> emptyList = userStorage.findAll();
        assertThat(emptyList).isEmpty();

        // Добавим пару пользователей
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1980, 5, 10));
        userStorage.create(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1992, 3, 15));
        userStorage.create(user2);

        // Проверим, что оба отдаются методом findAll()
        Collection<User> users = userStorage.findAll();
        assertThat(users).hasSize(2);
        assertThat(users).extracting("login").containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    @DisplayName("Проверяем обновление пользователя")
    void testUpdateUser() {
        // Создадим и сохраним
        User user = new User();
        user.setEmail("old@example.com");
        user.setLogin("oldlogin");
        user.setName("Old Name");
        user.setBirthday(LocalDate.of(1970, 1, 1));
        user = userStorage.create(user);

        // Обновим
        user.setEmail("new@example.com");
        user.setLogin("newlogin");
        user.setName("New Name");
        userStorage.update(user);

        // Проверим
        User updated = userStorage.findById(user.getId());
        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getLogin()).isEqualTo("newlogin");
        assertThat(updated.getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Проверяем удаление пользователя")
    void testDeleteUser() {
        // Создадим и сохраним
        User user = new User();
        user.setEmail("delete@example.com");
        user.setLogin("deleteMe");
        user.setName("WillBeDeleted");
        user.setBirthday(LocalDate.of(1999, 9, 9));
        user = userStorage.create(user);

        // Удаляем
        userStorage.delete(user.getId());

        // Проверяем, что при поиске бросается NotFoundException
        User finalUser = user;
        assertThatThrownBy(() -> userStorage.findById(finalUser.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Проверяем работу с друзьями (односторонняя дружба)")
    void testAddAndRemoveFriend() {
        // Создадим двух пользователей
        User user1 = new User();
        user1.setEmail("u1@example.com");
        user1.setLogin("u1");
        user1.setName("User1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        user1 = userStorage.create(user1);

        User user2 = new User();
        user2.setEmail("u2@example.com");
        user2.setLogin("u2");
        user2.setName("User2");
        user2.setBirthday(LocalDate.of(1991, 2, 2));
        user2 = userStorage.create(user2);

        // user1 добавляет user2 в друзья
        userStorage.addFriend(user1.getId(), user2.getId());
        // Список друзей user1 должен содержать user2
        assertThat(userStorage.getFriends(user1.getId()))
                .hasSize(1)
                .extracting("id")
                .containsExactly(user2.getId());
        // Список друзей user2 при односторонней дружбе — пуст
        assertThat(userStorage.getFriends(user2.getId())).isEmpty();

        // Удаляем user2 из друзей user1
        userStorage.removeFriend(user1.getId(), user2.getId());
        assertThat(userStorage.getFriends(user1.getId())).isEmpty();
    }
}
