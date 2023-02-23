package account;

import Enums.RoleEnum;
import ResourcesFiles.Movie;
import ResourcesFiles.Movies;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString

public class Account extends User {
    List<Movie> appreciatedMovies = new ArrayList<>();
    File appreciatedMoviesFile = new File("src/main/java/ResourcesFiles/AppreciatedMovies.txt");

    public Account(String username, String nickname, String password, String role, Integer age) {
        super(username, nickname, password, role, age);
        getUserAppreciatedMovies();
    }

    private void getUserAppreciatedMovies() {
        if (role.getValue() == RoleEnum.guest.getValue()) {
            return;
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(appreciatedMoviesFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username) && parts.length == 2) {
                    for (int i = 0; i < Integer.parseInt(parts[1]); i++) {
                        line = bufferedReader.readLine();
                        String[] movies = line.split((":"));
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
                        Date releaseDate = simpleDateFormat.parse(movies[2]);
                        Movie movie = Movie.builder()
                                .name(movies[0])
                                .category(movies[1])
                                .releaseDate(releaseDate)
                                .director((movies[3]))
                                .movieStudio((movies[4]))
                                .upVotes(Integer.parseInt(movies[5]))
                                .build();
                        appreciatedMovies.add(movie);
                    }
                    bufferedReader.close();
                    break;
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void addAppreciatedMovie(Movie movie) {
        if (role.getValue() == RoleEnum.guest.getValue()) {
            return;
        }

        List<String> updatedLines = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(appreciatedMoviesFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                updatedLines.add(line);
            }
            bufferedReader.close();
            int userIndex = -1;
            for (int i = 0; i < updatedLines.size(); i++) {
                if (updatedLines.get(i).startsWith(username + ":")) {
                    userIndex = i;
                    break;
                }
            }
            if (userIndex == -1) {
                updatedLines.add(username + ":" + 0);
                userIndex = updatedLines.size() - 1;
            }

            String[] parts = updatedLines.get(userIndex).split(":");

            if (parts[0].equals(username) && parts.length == 2) {
                int numberOfAppreciatedMovies = Integer.parseInt(parts[1]) + 1;

                for (int i = userIndex + 1; i < userIndex + numberOfAppreciatedMovies; i++) {
                    if (updatedLines.get(i).startsWith(movie.getName() + ":")) {
                        return;
                    }
                }
                movie.setUpVotes(movie.getUpVotes() + 1);
                updatedLines.set(userIndex, username + ":" + numberOfAppreciatedMovies);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
                String updatedLine = movie.getName() + ":" + movie.getCategory() + ":" + simpleDateFormat.format(movie.getReleaseDate()) + ":" + movie.getDirector() + ":" + movie.getMovieStudio() + ":" + movie.getUpVotes();
                updatedLines.add(userIndex + 1, updatedLine);
                appreciatedMovies.add(movie);
                for (int i = 0; i < updatedLines.size(); i++) {
                    if (updatedLines.get(i).startsWith(movie.getName() + ":")) {
                        updatedLines.set(i, updatedLine);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Files.write(appreciatedMoviesFile.toPath(), updatedLines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeAppreciatedMovie(Movie movie) {
        if (role.getValue() == RoleEnum.guest.getValue()) {
            return;
        }
        if (movie == null) {
            System.out.println("The movie is invalid! ");
            return;
        }
        List<String> updatedLines = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(appreciatedMoviesFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                updatedLines.add(line);
            }
            bufferedReader.close();
            int userIndex = -1;
            for (int i = 0; i < updatedLines.size(); i++) {
                if (updatedLines.get(i).startsWith(username + ":")) {
                    userIndex = i;
                    break;
                }
            }
            if (userIndex == -1) {
                updatedLines.add(username + ":" + 0);
                userIndex = updatedLines.size() - 1;
            }

            String[] parts = updatedLines.get(userIndex).split(":");

            if (parts[0].equals(username) && parts.length == 2) {
                int numberOfAppreciatedMovies = Integer.parseInt(parts[1]);

                boolean foundMovie = false;
                int movieIndex = userIndex;
                for (int i = userIndex; i <= userIndex + numberOfAppreciatedMovies; i++) {
                    if (updatedLines.get(i).startsWith(movie.getName() + ":")) {
                        foundMovie = true;
                        movieIndex = i;
                    }
                }
                if (foundMovie == false) {
                    return;
                }
                numberOfAppreciatedMovies--;
                updatedLines.set(userIndex, username + ":" + numberOfAppreciatedMovies);
                updatedLines.remove(movieIndex);
                appreciatedMovies.remove(movie);
                movie.setUpVotes(movie.getUpVotes() - 1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
                String updatedLine = movie.getName() + ":" + movie.getCategory() + ":" + simpleDateFormat.format(movie.getReleaseDate()) + ":" + movie.getDirector() + ":" + movie.getMovieStudio() + ":" + movie.getUpVotes();

                for (int i = 0; i < updatedLines.size(); i++) {
                    if (updatedLines.get(i).startsWith(movie.getName() + ":")) {
                        updatedLines.set(i, updatedLine);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Files.write(appreciatedMoviesFile.toPath(), updatedLines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
