package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validateUser(user);
        if (userStorage.getUserById(user.getId()) == null) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        return userStorage.update(user);
    }

    public User getUserById(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return user;
    }

    public boolean addFriend(Long userId, Long friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        return userStorage.addFriend(userId, friendId);
    }

    public boolean removeFriend(Long userId, Long friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        return userStorage.removeFriend(userId, friendId);
    }

    public Collection<User> getFriends(Long id) {
        if (userStorage.getUserById(id) == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        if (userStorage.getUserById(id) == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        if (userStorage.getUserById(otherId) == null) {
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден");
        }
        return userStorage.getCommonFriends(id, otherId);
    }

    public void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
