import random
import time

def generate_sql_script():
    filename = "init_database.sql"
    total_records = 10_000_000
    total_users = 1_000_000
    chunk_size = 10_000  # We write 10,000 rows at a time to prevent memory crashes
    
    # TMDB has hundreds of thousands of movies. 
    # We will use a pool of 100,000 valid-looking integer IDs.
    movie_id_pool = list(range(100, 100100)) 

    print(f"Starting generation of {total_records} records... This may take a minute.")
    start_time = time.time()

    with open(filename, 'w') as f:
        # 1. Write the Database Schema
        f.write("CREATE DATABASE IF NOT EXISTS movie_ratings_db;\n")
        f.write("USE movie_ratings_db;\n\n")
        
        f.write("CREATE TABLE user_ratings (\n")
        f.write("    id INT AUTO_INCREMENT PRIMARY KEY,\n")
        f.write("    user_id VARCHAR(255) NOT NULL,\n")
        f.write("    movie_id VARCHAR(255) NOT NULL,\n")
        f.write("    rating INT NOT NULL,\n")
        f.write("    UNIQUE KEY unique_user_movie (user_id, movie_id)\n") # Enforces uniqueness!
        f.write(");\n\n")
        
        # 2. Add the Index for fast querying
        f.write("-- Adding an index on user_id to heavily optimize read speeds\n")
        f.write("CREATE INDEX idx_user_id ON user_ratings(user_id);\n\n")

        # 3. Generate Data
        f.write("-- Inserting mock data\n")
        records_generated = 0
        user_id = 1
        values_chunk = []
        
        while records_generated < total_records:
            # Randomly assign between 1 and 19 ratings per user (average is 10)
            if user_id == total_users:
                # The last user gets whatever is left to hit exactly 10,000,000
                num_ratings = total_records - records_generated
            else:
                num_ratings = random.randint(1, 19)
                num_ratings = min(num_ratings, total_records - records_generated)
            
            # random.sample guarantees we don't pick the same movie twice for a user
            selected_movies = random.sample(movie_id_pool, num_ratings)
            
            for movie_id in selected_movies:
                rating = random.randint(1, 10)
                # Note: user_id and movie_id are strings in your Java model, so we wrap them in quotes
                values_chunk.append(f"('{user_id}', '{movie_id}', {rating})")
                records_generated += 1
                
                # Bulk insert chunking for database speed
                if len(values_chunk) >= chunk_size:
                    f.write("INSERT INTO user_ratings (user_id, movie_id, rating) VALUES\n")
                    f.write(",\n".join(values_chunk) + ";\n")
                    values_chunk = []
            
            user_id += 1
            
            # Edge case loop reset if we hit 1M users but haven't reached 10M records
            if user_id > total_users and records_generated < total_records:
                user_id = 1 

        # Insert any leftover records
        if values_chunk:
            f.write("INSERT INTO user_ratings (user_id, movie_id, rating) VALUES\n")
            f.write(",\n".join(values_chunk) + ";\n")

    end_time = time.time()
    print(f"Success! {records_generated} records written to {filename} in {round(end_time - start_time, 2)} seconds.")

if __name__ == "__main__":
    generate_sql_script()
