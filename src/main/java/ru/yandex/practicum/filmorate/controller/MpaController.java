package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaDbStorage mpaStorage;

    public MpaController(@Qualifier("mpaDbStorage") MpaDbStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @GetMapping
    public List<MpaRating> findAll() {
        return mpaStorage.findAll();
    }

    @GetMapping("/{id}")
    public MpaRating findById(@PathVariable Long id) {
        return mpaStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("MPA рейтинг не найден с id=" + id));
    }
}
