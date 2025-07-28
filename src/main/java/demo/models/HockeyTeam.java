package demo.models;

public class HockeyTeam {
    public long epochTime;
    public String teamName;
    public String year;
    public String winPercentage;

    public HockeyTeam(long epochTime, String teamName, String year, String winPercentage) {
        this.epochTime = epochTime;
        this.teamName = teamName;
        this.year = year;
        this.winPercentage = winPercentage;
    }
}
