package demo;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import demo.wrappers.Wrappers;

public class TestCases {
    ChromeDriver driver;
    Wrappers wrappers;

    @Test
    public void testCase01() throws InterruptedException {
        System.out.println("TestCase01 start: Success");
        // step : Open the website

        driver.get("https://www.scrapethissite.com/pages/");
        Thread.sleep(2000);
        System.out.println("Test step: Opened the website successfully");
        // step : Click on the "Hockey Teams" link

        wrappers.clickByLinkText("Hockey Teams");
        System.out.println("Test step: Clicked on the Hockey Teams link successfully");
        Thread.sleep(2000);

        // step : Scrape the data
        ArrayList<HashMap<String, Object>> teamList = new ArrayList<>(); // Initialize the list to hold Hashmap

        WebElement clickOnPage = driver.findElement(By.xpath("(//ul[@class='pagination']/li/a)[1]"));
        clickOnPage.click(); // Click on the first page to load the table
        Thread.sleep(2000); // Wait for the page to load

        System.out.println("Test step: Scraping data from the table");
        for (int i = 1; i <= 4; i++) { // loops used to inspect first pages
            List<WebElement> rows = driver.findElements(By.xpath("//tr[@class='team']")); // Get all rows in the table

            for (WebElement row : rows) {

                String teamName = row.findElement(By.xpath("./td[@class='name']")).getText(); // Get the team name
                int year = Integer.parseInt(row.findElement(By.xpath("./td[@class='year']")).getText()); // Get the year
                double winPercentage = Double
                        .parseDouble(row.findElement(By.xpath("./td[contains(@class,'pct')]")).getText()); // Get the win percentage

                long epoch = System.currentTimeMillis() / 1000; // Get the current epoch time in seconds
                String epochTime = String.valueOf(epoch); // Convert epoch time to String

                if (winPercentage < 0.4) {
                    // create HashMap to store team data
                    HashMap<String, Object> dataMap = new HashMap<>();
                    dataMap.put("epochTime", epochTime);
                    dataMap.put("teamName", teamName);
                    dataMap.put("year", year);
                    dataMap.put("winPercentage", winPercentage);

                    // add the dataMap to the teamList
                    teamList.add(dataMap);
                }
                

            }
            if (i < 4) {
                wrappers.clickNextButton(); // Click on the next button to go to the next page
                Thread.sleep(2000); // Wait for the next page to load
            }

            
        }

        System.out.println("Test step: Pages 1 to 4 scraped successfully, found " + teamList.size()
                + " teams with win percentage < 0.40");
        // print collected data
        for (HashMap<String, Object> teamData : teamList) {
            System.out.println("Epoch Time: " + teamData.get("epochTime") + "Team: " + teamData.get("teamName")
                    + ", Year: " + teamData.get("year")
                    + ", Win Percentage: " + teamData.get("winPercentage"));
        }

        ObjectMapper mapper = new ObjectMapper(); // objectMapper is a class from jackson libraray used to convert Java

        try {
            String userDir = System.getProperty("user.dir");
            File jsonFile = new File(userDir + "/src/test/resources/hockey-team-data.json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, teamList); // Write the list to a JSON file
            System.out.println("Test step: JSON data written to : " + jsonFile.getAbsolutePath());
            assertTrue(jsonFile.length() != 0); // Assert that the file is created
            // and not empty
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create hockey-team-data.json: " + e.getMessage());
        }
        System.out.println("TestCase01 end: Success");
    }

    // Move these methods outside of testCase01, to be class-level methods

    @Test
    public void testCase02() throws InterruptedException {
        System.out.println("TestCase02 start: Success");

        // step : Open the website
        driver.get("https://www.scrapethissite.com/pages/");
        Thread.sleep(2000);
        System.out.println("Test step: Opened the website successfully");

        // step : Click on the "Oscar Movies" link
        wrappers.clickByLinkText("Oscar");
        System.out.println("Test step: Clicked on the Oscar Movies link successfully");
        Thread.sleep(2000);
        // step : Scrape the data
        System.out.println("Test step: Scraping data from the table");

        ArrayList<HashMap<String, String>> movieList = new ArrayList<>(); // Initialize the list to hold hashmap

        List<WebElement> years = wrappers.getElements(By.xpath("//a[contains(@href, '#')]"));

        for (WebElement year : years) {
            String yearText = wrappers.getText(year);
            wrappers.clickOnElement(By.linkText(yearText));
            Thread.sleep(2000); // Wait for the page to load

            List<WebElement> rows = driver.findElements(By.xpath("//tr[@class='film']")); // Get all rows in the table

            int maxMovies = Math.min(5, rows.size()); // Limit to 5 movies per year section

            for (int i = 0; i < maxMovies; i++) {
                WebElement row = rows.get(i); // Get the current row
                String filmTitle = row.findElement(By.xpath("./td[contains(@class,'title')]")).getText(); // Get the film title
                                                            
                String nomination = row.findElement(By.xpath("./td[contains(@class,'nominations')]")).getText(); // Get the nomonations
                                                                                                                 
                String awards = row.findElement(By.xpath("./td[contains(@class,'awards')]")).getText(); // Get the awards
                                                                                                        
                boolean isWinnerFlag = (i == 0); // Check if the film is a winner (first row in the year section)
                String isWinner = String.valueOf(isWinnerFlag); // Convert boolean to String

                long epoch = System.currentTimeMillis() / 1000; // Get the current epoch time in seconds
                String epochTime = String.valueOf(epoch); // Convert epoch time to String

                HashMap<String, String> movie = new HashMap<>();
                movie.put("epochTime", epochTime);
                movie.put("year", yearText);
                movie.put("title", filmTitle);
                movie.put("nominations", nomination);
                movie.put("awards", awards);
                movie.put("isWinner", isWinner); // Add the isWinner flag to the movie object

                movieList.add(movie); // Add the movie object to the list
            }
            System.out.println("Completed processing year: " + yearText);
            System.out.println("------------------------------------------------------");

        }

        System.out.println("Final collected data:");
        for (HashMap<String, String> movieData : movieList) {
            System.out.println("Epoch Time: " + movieData.get("epochTime") + ", Year: " + movieData.get("year")
                    + ", Title: " + movieData.get("title") + ", Nominations: " + movieData.get("nominations")
                    + ", Awards: " + movieData.get("awards") + ", Is Winner: " + movieData.get("isWinner"));
        }

        ObjectMapper mapper = new ObjectMapper(); // objectMapper is a class from jackson libraray used to convert Java
                                                  // objects to JSON
        try {
            String userDir = System.getProperty("user.dir");
            File jsonFile = new File(userDir + "/src/test/resources/oscar-winner-data.json"); // it will create a file

            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, movieList); // Write the list to a JSON file
            System.out.println("Test step: JSON data written to : " + jsonFile.getAbsolutePath());
            assertTrue(jsonFile.exists() && jsonFile.length() > 0,
                    "oscar-winner-data.json should exist and not be empty"); // empty
            System.out.println("Test step: oscar-winner-data.json created with " + movieList.size() + " records");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create oscar-winner-data.json: " + e.getMessage());
        }

        System.out.println("TestCase02 end: Success");
    }

    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");
        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        wrappers = new Wrappers(driver);
    }

    @AfterTest
    public void endTest() {
        driver.close();
        driver.quit();
    }

}
