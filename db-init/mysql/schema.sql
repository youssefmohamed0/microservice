CREATE TABLE user_ratings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    movie_id VARCHAR(255) NOT NULL,
    rating INT NOT NULL,
    UNIQUE KEY unique_user_movie (user_id, movie_id)
);

-- Adding an index on user_id to heavily optimize read speeds
CREATE INDEX idx_user_id ON user_ratings(user_id);
