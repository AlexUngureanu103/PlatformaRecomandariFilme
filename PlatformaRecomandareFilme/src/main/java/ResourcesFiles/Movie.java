package ResourcesFiles;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Movie {
    private String name;
    private String category;
    private Date releaseDate;
    private String director;
    private String movieStudio;
    private int upVotes;

    public void displayMovie() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
        Stream<String> streamOf = Stream.of(name, " | ", category, " | ", simpleDateFormat.format(releaseDate), " | ", Integer.toString(upVotes));

        streamOf.forEach(System.out::print);
        System.out.println();
    }
}
