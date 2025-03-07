-- Создаем таблицы, если их ещё нет

CREATE TABLE IF NOT EXISTS users (
                                     id INT PRIMARY KEY AUTO_INCREMENT,
                                     email VARCHAR(255) NOT NULL,
                                     login VARCHAR(50) NOT NULL,
                                     name VARCHAR(255),
                                     birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS friendships (
                                           user_id INT NOT NULL,
                                           friend_id INT NOT NULL,
                                           status VARCHAR(20),
                                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           PRIMARY KEY (user_id, friend_id),
                                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                           FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genres (
                                      id INT PRIMARY KEY AUTO_INCREMENT,
                                      name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
                                           id INT PRIMARY KEY AUTO_INCREMENT,
                                           name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
                                     id INT PRIMARY KEY AUTO_INCREMENT,
                                     name VARCHAR(255) NOT NULL,
                                     description TEXT,
                                     release_date DATE NOT NULL,
                                     duration INT NOT NULL,
                                     mpa_id INT NOT NULL,
                                     FOREIGN KEY (mpa_id) REFERENCES mpa_ratings(id)
);

CREATE TABLE IF NOT EXISTS film_genres (
                                           film_id INT NOT NULL,
                                           genre_id INT NOT NULL,
                                           PRIMARY KEY (film_id, genre_id),
                                           FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
                                           FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes (
                                     film_id INT NOT NULL,
                                     user_id INT NOT NULL,
                                     PRIMARY KEY (film_id, user_id),
                                     FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
                                     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
