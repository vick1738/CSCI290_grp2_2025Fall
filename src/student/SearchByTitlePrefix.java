package student;

import java.util.stream.Stream;

public class SearchByTitlePrefix {

    private SongCollection collection;

    /**
     * Constructor that takes an existing SongCollection
     */
    public SearchByTitlePrefix(SongCollection sc) {
        this.collection = sc;
    }

    /**
     * Searches for all songs whose TITLES begin with the given prefix.
     *
     * @param prefix the title prefix to search for (case-insensitive)
     * @return array of matching songs
     */
    public Song[] search(String prefix) {
        return collection.getByTitlePrefix(prefix);
    }

    /**
     * Main method for testing the search by title prefix.
     * Usage: prog songfile prefix
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("usage: prog songfile prefix");
            return;
        }

        // Load the song file
        SongCollection sc = new SongCollection(args[0]);
        SearchByTitlePrefix sbtp = new SearchByTitlePrefix(sc);

        // Perform search if prefix is provided
        if (args.length > 1) {
            System.out.println("searching for title prefix: " + args[1]);
            Song[] byTitleResult = sbtp.search(args[1]);

            // Display results
            System.out.println("Total matches: " + byTitleResult.length);
            System.out.println("First 10 matches:");
            Stream.of(byTitleResult).limit(10).forEach(System.out::println);
        }
    }
}
