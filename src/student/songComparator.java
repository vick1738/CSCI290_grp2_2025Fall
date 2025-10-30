package student;

import java.util.Comparator;

public class songComparator implements Comparator<Song> {
    @Override
    public int compare(Song s1, Song s2) {
        int titleCmp = s1.getTitle().compareToIgnoreCase(s2.getTitle());
        if (titleCmp != 0) return titleCmp;
        return s1.getArtist().compareToIgnoreCase(s2.getArtist());
    }

}
