package demo.models;

public class OscarMovie {
    public long epochTime;
    public String year;
    public String title;
    public int nominations;
    public int awards;
    public String isWinner;

    public OscarMovie(long epochTime, String year, String title, int nominations, int awards, String isWinner) {
        this.epochTime = epochTime;
        this.year = year;
        this.title = title;
        this.nominations = nominations;
        this.awards = awards;
        this.isWinner = isWinner;
    }
}
