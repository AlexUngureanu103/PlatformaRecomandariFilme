package ResourcesFiles;

import Structures.Pair;
import lombok.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@Data
public class Movies {
    List<Movie> moviesList = new ArrayList<>();
    HashSet<String> availableMovies = new HashSet<>();
    File movieFile = new File("src/main/java/ResourcesFiles/MoviesFile.txt");
    File addMovieFromFile = new File("src/main/java/ResourcesFiles/AddMoviesFromFile.txt");

    public Movies() {
        readMovieFile();
    }

    private void readMovieFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(movieFile))) {
            String line;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
            int numOfFields = 6;

            while ((line = bufferedReader.readLine()) != null) {
                String[] movieData = line.split(":");

                Date releaseDate = simpleDateFormat.parse(movieData[2]);
                Movie movie = Movie.builder()
                        .name(movieData[0])
                        .category(movieData[1])
                        .releaseDate(releaseDate)
                        .director((movieData[3]))
                        .movieStudio((movieData[4]))
                        .upVotes(Integer.parseInt(movieData[5]))
                        .build();
                moviesList.add(movie);
                availableMovies.add(movieData[0]);
            }
            Collections.sort(moviesList, (m1, m2) -> m2.getUpVotes() - m1.getUpVotes());

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void displayTopMoviePageN(Integer n) {
        if (n > moviesList.size() / 10) {
            n = moviesList.size() / 10;
        } else if (n <= 1) {
            n = 1;
        }
        int startIndex = (n - 1) * 10;
        int endIndex = startIndex + 10;

        for (int i = startIndex; i < endIndex; i++) {
            moviesList.get((i)).displayMovie();
        }
    }

    public void UpdateAMovie(String movieName, Integer value) {
        Movie updatedMovie;
        for (Movie movie : moviesList) {
            if (movie.getName().equals((movieName))) {
                movie.setUpVotes((movie.getUpVotes() + value));
                updatedMovie = movie;
                break;
            }
        }
        List<String> updatedLines = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        for (Movie movie : moviesList) {
            updatedLines.add(movie.getName() + ":" + movie.getCategory() + ":" + simpleDateFormat.format(movie.getReleaseDate()) + ":" + movie.getDirector() + ":" + movie.getMovieStudio() + ":" + movie.getUpVotes());
        }
        try {
            Files.write(movieFile.toPath(), updatedLines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Movie getMovieByName(String movieName) {
        List<Pair<Movie, Double>> moviesFound = new ArrayList<>();

        for (Movie movie : moviesList) {
            Double currentAccuracy = findSimilarities(movie.getName(), movieName);
            if (currentAccuracy > 0.6) {
                moviesFound.add(new Pair<>(movie, currentAccuracy));
            }
        }
        moviesFound.sort(new Comparator<Pair<Movie, Double>>() {
            @Override
            public int compare(Pair<Movie, Double> o1, Pair<Movie, Double> o2) {
                return Double.compare(o2.getSecond(), o1.getSecond());
            }
        });

        if (!moviesFound.isEmpty()) {
            return moviesFound.get(0).getFirst();
        }
        return null;
    }

    public List<Movie> getMoviesByCategory(String category, Optional<List<Movie>> movies) {
        List<Movie> interestCategory = new ArrayList<>();
        List<Movie> availableMovies = movies.orElse(moviesList);
        for (Movie movie : availableMovies) {
            Double accuracy = findSimilarities(category, movie.getCategory());
            if (accuracy > 0.8) {
                interestCategory.add(movie);
            }
        }
        return interestCategory;
    }

    public List<Movie> sortByCategory(List<Movie> movies, String category) {
        movies.sort(new Comparator<Movie>() {
            @Override
            public int compare(Movie o1, Movie o2) {
                return Double.compare(findSimilarities(o2.getCategory(), category), findSimilarities(o1.getCategory(), category));
            }
        });

        return movies;
    }

    public List<Movie> searchMoviesByName(String movieName) {
        List<Pair<Movie, Double>> moviesFound = new ArrayList<>();

        for (Movie movie : moviesList) {
            Double currentAccuracy = findSimilarities(movie.getName(), movieName);
            if (currentAccuracy > 0.5) {
                moviesFound.add(new Pair<>(movie, currentAccuracy));
            }
        }
        moviesFound.sort(new Comparator<Pair<Movie, Double>>() {
            @Override
            public int compare(Pair<Movie, Double> o1, Pair<Movie, Double> o2) {
                return Double.compare(o2.getSecond(), o1.getSecond());
            }
        });

        List<Movie> moviesOrderedByAccuracy = new ArrayList<>();
        for (int i = 0; i < moviesFound.size(); i++) {
            moviesOrderedByAccuracy.add(moviesFound.get(i).getFirst());
        }
        return moviesOrderedByAccuracy;
    }

    private static Double findSimilarities(String x, String y) {
        if (x == null | y == null) {
            throw new IllegalArgumentException("String can't be null");
        }
        Double maxLenght = Double.max(x.length(), y.length());
        if (maxLenght > 0) {
            return (maxLenght - getLevenshteinDistance(x, y)) / maxLenght;
        }
        return 1.0;
    }

    private static int getLevenshteinDistance(String X, String Y) {
        int m = X.length();
        int n = Y.length();

        int[][] T = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            T[i][0] = i;
        }
        for (int j = 1; j <= n; j++) {
            T[0][j] = j;
        }

        int cost;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                cost = X.charAt(i - 1) == Y.charAt(j - 1) ? 0 : 1;
                T[i][j] = Integer.min(Integer.min(T[i - 1][j] + 1, T[i][j - 1] + 1),
                        T[i - 1][j - 1] + cost);
            }
        }

        return T[m][n];
    }

    public void addAMovie(Movie movie) {
        if (availableMovies.contains(availableMovies)) {
            return;
        }
        moviesList.add(movie);
        availableMovies.add(movie.getName());
        updateTheFile();
    }

    public void addMoviesFromFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(addMovieFromFile))) {
            String line;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
            int numOfFields = 6;
            System.out.println("Successfully added movies :");
            while ((line = bufferedReader.readLine()) != null) {
                String[] movieData = line.split(":");

                Date releaseDate = simpleDateFormat.parse(movieData[2]);
                if (!availableMovies.contains(movieData[0])) {
                    Movie movie = Movie.builder()
                            .name(movieData[0])
                            .category(movieData[1])
                            .releaseDate(releaseDate)
                            .director((movieData[3]))
                            .movieStudio((movieData[4]))
                            .upVotes(Integer.parseInt(movieData[5]))
                            .build();
                    moviesList.add(movie);
                    availableMovies.add(movieData[0]);
                    Stream<String> StreamOfArray = Arrays.stream(movieData);
                    StreamOfArray.forEach(System.out::println);
                    System.out.println();
                }
            }
            Collections.sort(moviesList, (m1, m2) -> m2.getUpVotes() - m1.getUpVotes());
            updateTheFile();
            bufferedReader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void updateTheFile() {
        try {
            List<String> updatedLines = new ArrayList<>();
            String line;
            for (Movie movie : moviesList) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
                line = movie.getName() + ":" + movie.getCategory() + ":" + simpleDateFormat.format(movie.getReleaseDate()) + ":" + movie.getDirector() + ":" + movie.getMovieStudio() + ":" + movie.getUpVotes();
                updatedLines.add(line);
            }
            Files.write(movieFile.toPath(), updatedLines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
