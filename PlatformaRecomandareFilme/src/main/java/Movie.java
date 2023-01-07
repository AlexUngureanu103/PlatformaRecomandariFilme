import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Movie {
    private String Name;
    private String category;
    private Date releaseDate;
    private Integer Upvotes;
    private Integer Downvotes;
}
