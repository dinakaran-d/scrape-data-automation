package demo.wrappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */

     WebDriver driver;
     WebDriverWait wait;

    public Wrappers(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    public void clickByLinkText(String linkText) {
        wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText(linkText))).click();
    }

    public List<WebElement> getTableRows() {
        WebElement tbody = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.table tbody")));
        return tbody.findElements(By.tagName("tr"));
    }

    public void clickNextButton() {
        WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@aria-label, 'Next')]")));
        nextButton.click();
    }

    public String getCellText(WebElement row, int cellIndex) {
        // Ensure the row and cellIndex are valid before returning the text
        List<WebElement> cells = row.findElements(By.tagName("td"));
        if (cellIndex < 0 || cellIndex >= cells.size()) {
            throw new IndexOutOfBoundsException("Cell index out of bounds");
        }
        return cells.get(cellIndex).getText();
    }


    public void clickOnElement(By locator) {
        driver.findElement(locator).click();
    }

    public List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }

    public String getText(WebElement element) {
        return element.getText().trim();
    }

    public static boolean isBestPictureWinner(WebElement row) {
        WebElement bestPictureCell = row.findElement(By.cssSelector("td:nth-child(4)"));
        String cellContent = bestPictureCell.getAttribute("innerHTML").trim();
        return !cellContent.isEmpty();
    }

    public static int parseInt(String text) {
        try{
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            System.out.println("Error parsing integer from text: " + text);
            return 0; // or throw an exception based on your needs
        }
    }




}
