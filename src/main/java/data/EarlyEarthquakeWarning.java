package data;


import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.time.Duration;

public class EarlyEarthquakeWarning {

	/*
	 * Uses google trends to attempt to predict if an earthquake is about to happen
	 * 
	 * Because most people when they feel an earthquake go to social media first
	 * 
	 */
	
	public EarlyEarthquakeWarning() {
		
	}
	
	
	/*
	 * Parses csv file from google trends
	 * 
	 * doc for selenium : https://www.selenium.dev/documentation/en/
	 *  possible queries:
	 *  
	 * https://trends.google.com/trends/explore?date=now%207-d&geo=US&q=earthquake
	 * https://trends.google.com/trends/explore?date=now%201-d&geo=US&q=earthquake
	 * 
	 * 
	 * https://trends.google.com/trends/explore?date=now%207-d&geo=US&q=earthquake%20united%20states
	 * 
	 * https://trends.google.com/trends/explore?date=now%201-H&geo=US&q=did%20an%20earthquake%20just%20happen 
	 * 
	 * ^ thats a good one the past hour for "did an earthquake just happen"
	 * 
	 * 
	 * 
	 * <i class="material-icons-extended gray">file_download</i>
	 * 
	 * 
	 */
	public boolean earlyWarning() {
		
		WebDriver driver = new FirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        try {
            driver.get("https://google.com");
            driver.findElement(By.name("q")).sendKeys("cheese" + Keys.ENTER);
            WebElement firstResult = wait.until(presenceOfElementLocated(By.cssSelector("h3>div")));
            System.out.println(firstResult.getAttribute("textContent"));
        } finally {
            driver.quit();
        }
        
		return false;
	}
}
