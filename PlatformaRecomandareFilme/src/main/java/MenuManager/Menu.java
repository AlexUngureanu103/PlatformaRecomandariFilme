package MenuManager;

import AuthentificationServices.Login;
import AuthentificationServices.Register;
import Enums.RoleEnum;
import ResourcesFiles.Movie;
import ResourcesFiles.Movies;
import account.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@SuperBuilder
@EqualsAndHashCode
public class Menu {
    File accountsData = new File("accountsData.txt");
    Register register;
    Login login;
    Account currentAccount;
    Movies movies;

    List<String> adminOptions = new ArrayList<>();

    List<String> guestOptions = new ArrayList<>();

    List<String> userOptions = new ArrayList<>();

    List<String> commandsAvailableForEveryone = new ArrayList<>();


    public Menu() {
        try {
            if (!accountsData.exists())
                accountsData.createNewFile();
            register = new Register(accountsData);
            login = new Login(accountsData);
            currentAccount = new Account();
            movies = new Movies();
            commandsAvailableForEveryone();
            verifiedUserCommands();
            adminOnlyCommands();
            guestOnlyCommands();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addAdmin(String username) {
        boolean found = false;
        List<String> usersData = new ArrayList<>();
        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(accountsData));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username)) {
                    found = true;
                    System.out.println("Username found!");
                    String updatedUser = parts[0] + ":" + parts[1] + ":" + parts[2] + ":" + parts[3] + ":" + RoleEnum.admin;
                    usersData.add(updatedUser);
                } else {
                    usersData.add(line);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!found)
            System.out.println("Invalid username!");
        else {
            try {
                Files.write(accountsData.toPath(), usersData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void displayMenuOptions() {
        System.out.println("\nMenu options for " + currentAccount.getRole().toString().toUpperCase() + " account type: ");
        displayAvailableCommands();
    }

    private void commandsAvailableForEveryone() {
        commandsAvailableForEveryone.add("search) Search for a movie"); //TBC
        commandsAvailableForEveryone.add("top-page) Display the n-th page of movies");
        commandsAvailableForEveryone.add("top) Display most liked movies");
        commandsAvailableForEveryone.add("feed) Display 10 recommended movies");
        commandsAvailableForEveryone.add("exit) Exit the application");
    }

    private void guestOnlyCommands() {
        guestOptions.add("register) Register a new account");
        guestOptions.add("login) Login into an existent account");
    }

    private void adminOnlyCommands() {
        adminOptions.add("add-movie) Add movie to platform");
        adminOptions.add("make-admin) Give an user the admin role");
    }

    private void verifiedUserCommands() {
        userOptions.add("logout) Logout");
        userOptions.add("add-fav) Add a movie to favorite");
        userOptions.add("rem-fav) Remove a movie from favorite");
        userOptions.add("display-fav) Display liked movies");

    }

    private void displayAvailableCommands() {
        displayOptions(commandsAvailableForEveryone);
        if (currentAccount.getRole().equals(RoleEnum.guest)) {
            displayOptions(guestOptions);
        }
        if (currentAccount.getRole().getValue() >= RoleEnum.user.getValue()) {
            displayOptions(userOptions);
        }
        if (currentAccount.getRole().equals(RoleEnum.admin)) {
            displayOptions(adminOptions);
        }
    }

    private void displayOptions(List<String> options) {
        Stream<String> streamFromCollection = options.stream();
        streamFromCollection.forEach(System.out::println);
    }

    public void displayMenu() {
        boolean exit = false;
        while (!exit) {
            try {
                displayMenuOptions();
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter an option : ");
                String option = scanner.nextLine();
                System.out.println();
                menuOptionManager(option);
                if (option.equals("exit")) {
                    exit = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void menuOptionManager(String option) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        switch (option) {
            case "search": {
                System.out.println("Available search methods: ");
                System.out.println("By name :N\nBy category :C\nBy name , but prioritizing the category:CN");
                System.out.print("provide the desired search methode: ");
                String searchOption = scanner.nextLine();
                searchOptions(searchOption);
                break;
            }
            case "register": {
                if (currentAccount.getRole() != RoleEnum.guest)
                    return;
                register.register();
                break;
            }
            case "login": {
                if (currentAccount.getRole() == RoleEnum.guest) {
                    currentAccount = login.login();
                }
                break;
            }
            case "logout": {
                if (currentAccount.getRole() != RoleEnum.guest) {
                    currentAccount = login.logout();
                }
                break;
            }
            case "top-page": {
                System.out.print("Please insert the desire page of movies: ");
                int page = Integer.parseInt(scanner.nextLine());
                movies.displayTopMoviePageN(page);
                break;
            }
            case "add-fav": {
                if (currentAccount.getRole() == RoleEnum.guest) {
                    return;
                }
                System.out.print("Please insert the name of a movie to add to your favorites: ");
                String movie = scanner.nextLine();
                currentAccount.addAppreciatedMovie(movies.getMovieByName(movie));
                movies.updateTheFile();
                break;
            }
            case "rem-fav": {
                if (!extractAndDisplayFavoriteMovies()) {
                    return;
                }
                System.out.print("Please insert the name of a movie to remove from your favorites: ");
                String movie = scanner.nextLine();
                currentAccount.removeAppreciatedMovie(movies.getMovieByName(movie));
                movies.updateTheFile();
                break;
            }
            case "display-fav": {
                extractAndDisplayFavoriteMovies();
                break;
            }
            case "exit": {
                currentAccount = login.logout();
                boolean exit = true;
                break;
            }
            case "top": {
                movies.displayTopMoviePageN(1);
                break;
            }
            case "feed": {
                List<Movie> userFeed = new ArrayList<>();
                userFeed = (getUserFeed(currentAccount.getAppreciatedMovies(), movies.getMoviesList()));
                Integer count = 0;
                while (count < userFeed.size()) {
                    System.out.println();
                    for (Integer counter = count; counter < userFeed.size() && counter < count + 10; counter++) {
                        userFeed.get(counter).displayMovie();
                    }

                    count += 10;
                    System.out.print("Display next page Y/N:");
                    String feedOption = scanner.nextLine();
                    if (!feedOption.equals("Y"))
                        break;
                }
                break;
            }
            case "make-admin": {
                if (currentAccount.getRole() != RoleEnum.admin) {
                    return;
                }
                System.out.println("Warning , this can't be UNDONE!");
                System.out.print("Continue N/Y :");
                String variant = scanner.nextLine();
                if (variant.equals("Y")) {
                    System.out.print("Insert the username to become admin: ");
                    variant = scanner.nextLine();
                    addAdmin(variant);
                }
                break;
            }
            case "add-movie": {
                if (currentAccount.getRole() != RoleEnum.admin) {
                    return;
                }
                System.out.println("Choose a methode to add movies :");
                System.out.println("file) From file");
                System.out.println("console) From console");
                System.out.print("Option: ");
                String addMovieOption = scanner.nextLine();
                if (addMovieOption.equals("file")) {
                    movies.addMoviesFromFile();
                } else if (addMovieOption.equals("console")) {

                    System.out.println("Add a new movie :");
                    System.out.print("Movie name: ");
                    Movie newMovie = new Movie();
                    newMovie.setName(scanner.nextLine());
                    System.out.print("Category/Genre: ");
                    newMovie.setCategory(scanner.nextLine());
                    System.out.println("Release date: FORMAT dd/mm/yyyy");
                    String input = "";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
                    while (!input.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d")) {
                        System.out.print("Input: ");
                        input = scanner.nextLine();
                    }
                    Date releaseDate = simpleDateFormat.parse(input);
                    newMovie.setReleaseDate(releaseDate);
                    System.out.print("Director: ");
                    newMovie.setDirector(scanner.nextLine());
                    System.out.print("Studio: ");
                    newMovie.setMovieStudio(scanner.nextLine());
                    System.out.print("Number of up votes: ");
                    newMovie.setUpVotes(Integer.parseInt(scanner.nextLine()));
                    movies.addAMovie(newMovie);
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    private void searchOptions(String searchOption) {
        Scanner scanner = new Scanner(System.in);
        switch (searchOption) {
            case "N": {
                System.out.print("Please insert the movie to search: ");
                String movieName = scanner.nextLine();
                List<Movie> moviesByName = movies.searchMoviesByName(movieName);

                for (Movie movie : moviesByName) {
                    movie.displayMovie();
                }
                break;
            }
            case "C": {
                System.out.print("Please insert a category: ");
                String category = scanner.nextLine();
                List<Movie> moviesByCategory = movies.getMoviesByCategory(category, Optional.empty());

                if (moviesByCategory.size() == 0) {
                    System.out.println("Invalid category !");
                    return;
                }

                for (Movie movie : moviesByCategory) {
                    movie.displayMovie();
                }
                break;
            }
            case "CN": {
                System.out.print("Please insert the movie to search: ");
                String movieName = scanner.nextLine();

                System.out.print("Please insert a category: ");
                String category = scanner.nextLine();
                List<Movie> movieList = movies.sortByCategory(movies.searchMoviesByName(movieName), category);
                for (Movie movie : movieList) {
                    movie.displayMovie();
                }
                break;
            }
            default:
                break;
        }
    }

    private boolean extractAndDisplayFavoriteMovies() {
        if (currentAccount.getRole() == RoleEnum.guest) {
            return false;
        }
        List<Movie> appreciatedMovies = currentAccount.getAppreciatedMovies();
        System.out.println("Your appreciated movies are: ");
        for (Movie movie : appreciatedMovies) {
            movie.displayMovie();
        }
        return true;
    }

    private List<Movie> getUserFeed(List<Movie> appreciatedMovies, List<Movie> movies) {
        Map<String, Long> preferredCategories = appreciatedMovies.stream()
                .collect(Collectors.groupingBy(Movie::getCategory, Collectors.counting()));

        return movies.stream()
                .sorted(Comparator.comparing((Movie
                                                      m) -> preferredCategories.getOrDefault(m.getCategory(), 0L).intValue())
                        .thenComparingInt(Movie::getUpVotes)
                        .thenComparing(Movie::getReleaseDate).reversed())
                .collect(Collectors.toList());
    }
}
