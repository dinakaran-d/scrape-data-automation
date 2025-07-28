package demo;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
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

import demo.models.HockeyTeam;
import demo.models.OscarMovie;
import demo.wrappers.Wrappers;
import dev.failsafe.internal.util.Assert;


public class TestCases {
    ChromeDriver driver;
    Wrappers wrappers;


    @Test
    public void testCase01() throws InterruptedException {
        System.out.println("TestCase01 start: Success");
        //step : Open the website
        driver.get("https://www.scrapethissite.com/pages/");
        Thread.sleep(2000);     
        System.out.println("Test step: Opened the website successfully");
        //step : Click on the "Hockey Teams" link
        wrappers.clickByLinkText("Hockey Teams");
        System.out.println("Test step: Clicked on the Hockey Teams link successfully");
        Thread.sleep(2000);
        //step : Scrape the data
        List<HockeyTeam> teamList = new ArrayList<>();
        System.out.println("Test step: Scraping data from the table");
        for(int i=0; i<4; i++) {
            List<WebElement> rows = wrappers.getTableRows();
           
            for(WebElement row : rows) {
               
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if(cells.size() < 6) {
                    continue; // Skip rows that do not have enough cells
                
                }
                String teamName = wrappers.getCellText(row, 0);
                String year = wrappers.getCellText(row, 1);
                String winPercentage = wrappers.getCellText(row, 5);

                try{
                    double winPerc = Double.parseDouble(winPercentage);
                    if(winPerc < 0.40) {
                        HockeyTeam team = new HockeyTeam(
                            Instant.now().getEpochSecond(),
                            teamName,
                            year,
                            winPercentage
                        );
                        teamList.add(team);
                    }
                }catch (NumberFormatException e) {
                    System.err.println("Invalid win percentage format for team: " + teamName + " in year: " + year);
                }
            }
            // Click the next button if not on the last page
            if(i < 3) {
                wrappers.clickNextButton();
                Thread.sleep(2000); // Wait for the next page to load
            }
            
        }

        System.out.println("Test step: Pages 1 to 4 scraped successfully, found " + teamList.size() + " teams with win percentage < 0.40");
            

        // Your scraping logic to fill teamList...

    // 👇 JSON creation code
    ObjectMapper mapper = new ObjectMapper();
    File jsonFile = new File("hockey-team-data.json");
    try {
        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, teamList);
        System.out.println("Test step: hockey-team-data.json created with " + teamList.size() + " records");
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
        //step : Open the website
        driver.get("https://www.scrapethissite.com/pages/");
        Thread.sleep(2000);     
        System.out.println("Test step: Opened the website successfully");
        //step : Click on the "Oscar Movies" link   
        wrappers.clickByLinkText("Oscar");
        System.out.println("Test step: Clicked on the Oscar Movies link successfully");
        Thread.sleep(2000);
        //step : Scrape the data
        System.out.println("Test step: Scraping data from the table");
        
        
        List<OscarMovie> movieList = new ArrayList<>();
    
        List<WebElement> years = wrappers.getElements(By.xpath("//a[contains(@href, '#')]"));

        for(WebElement year : years) {
            String yearText = wrappers.getText(year);
            wrappers.clickOnElement(By.linkText(yearText));
            Thread.sleep(2000); // Wait for the page to load

            List<WebElement> rows = wrappers.getTableRows();
            int count =0;
            for(WebElement row : rows) {
                if(count >= 5) break; // Limit to 5 records per year
                // Ensure the row has enough cells before accessing them
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if(cells.size() < 4) {
                    continue; // Skip rows that do not have enough cells
                }
                OscarMovie movie = new OscarMovie(
                    Instant.now().getEpochSecond(),
                    yearText,
                    wrappers.getCellText(row, 0),
                    Integer.parseInt(wrappers.getCellText(row, 1)),
                    Integer.parseInt(wrappers.getCellText(row, 2)),
                    String.valueOf(Wrappers.isBestPictureWinner(row)) // Convert boolean to String and access statically
                );
                movieList.add(movie);
                count++;
                System.out.println(yearText + " - " + wrappers.getCellText(row, 0) + " (Winner: " + String.valueOf(Wrappers.isBestPictureWinner(row)) + ")");
    
            }
            System.out.println("------------------------------------------------------");
        }



        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("oscar-winner-data.json"), movieList);
            System.out.println("Test step: oscar-winner-data.json created with " + movieList.size() + " records");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create oscar-winner-data.json: " + e.getMessage());
        }

        File jsonFile = new File("oscar-winner-data.json");
        Assert.isTrue(jsonFile.length() > 0 && jsonFile.exists(), "oscar-winner-data.json should exist after writing");
        System.out.println("TestCase02 end: Success");
    }

@BeforeTest
public void startBrowser()
{
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
public void endTest()
{
    driver.close();
    driver.quit();
}

}