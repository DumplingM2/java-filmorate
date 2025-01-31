package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);
    private final Map<Long, Set<Long>> friendships = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(idGenerator.incrementAndGet());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        friendships.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friendships.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
        return true;
    }

    @Override
    public boolean removeFriend(Long userId, Long friendId) {
        friendships.computeIfAbsent(userId, k -> new HashSet<>()).remove(friendId);
        friendships.computeIfAbsent(friendId, k -> new HashSet<>()).remove(userId);
        return true;
    }

    @Override
    public Collection<User> getFriends(Long id) {
        return friendships.getOrDefault(id, new HashSet<>()).stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        Set<Long> friends1 = friendships.getOrDefault(id, new HashSet<>());
        Set<Long> friends2 = friendships.getOrDefault(otherId, new HashSet<>());
        Set<Long> commonFriends = new HashSet<>(friends1);
        commonFriends.retainAll(friends2);
        return commonFriends.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }
}
