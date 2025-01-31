package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidationTest {

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidateUser_EmailIsBlank_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.now());

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.validateUser(user));

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    public void testValidateUser_EmailDoesNotContainAt_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("example.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.now());

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.validateUser(user));

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    public void testValidateUser_LoginIsBlank_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("example@example.com");
        user.setLogin("");
        user.setName("Name");
        user.setBirthday(LocalDate.now());

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.validateUser(user));

        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void testValidateUser_LoginContainsSpaces_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("example@example.com");
        user.setLogin("login with spaces");
        user.setName("Name");
        user.setBirthday(LocalDate.now());

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.validateUser(user));

        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void testValidateUser_BirthdayInFuture_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("example@example.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.validateUser(user));

        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}
