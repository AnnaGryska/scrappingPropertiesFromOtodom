
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class ScrapingData {
    WebDriver driver;

    @BeforeClass
    public void prepare() {
        System.setProperty("webdriver.chrome.driver", "./src/test/java/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.otodom.pl/");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(50));
        driver.findElement(By.id("onetrust-accept-btn-handler")).click();
    }

    @AfterClass
    public void closeBrowser() {
        driver.close();
    }

    @Test
    public void scrappingPropertiesFromOtodom() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(200));

        driver.findElement(By.className("css-z0vgrw")).click();
        WebElement apartmentOption = driver.findElement(By.xpath("//*[text()='Mieszkania']"));
        apartmentOption.click();

        driver.findElement(By.className("react-select__value-container")).click();
        WebElement forSalesOption = driver.findElement(By.xpath("//*[text()='Na sprzedaż']"));
        forSalesOption.click();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(50));

        WebElement minApartmentArea = driver.findElement(By.id("areaMin"));
        minApartmentArea.sendKeys("40");

        WebElement maxApartmentArea = driver.findElement(By.id("areaMax"));
        maxApartmentArea.sendKeys("100");

        WebElement searchButton = driver.findElement(By.id("search-form-submit"));
        searchButton.click();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(50));

        WebElement otodomInfoModalExitButton = driver.findElement(By.className("css-jjrhw1"));
        otodomInfoModalExitButton.click();

        WebElement webNotificationsExitButton = driver.findElement(By.className("eels1pm3"));
        webNotificationsExitButton.click();

        JavascriptExecutor jse = (JavascriptExecutor)driver;
        jse.executeScript("window.scrollTo(0, document.body.scrollHeight)"); // loading all elements

        List<WebElement> listingItems = driver.findElements(By.cssSelector("[data-cy='listing-item-link']"));

        JSONArray allProperties = new JSONArray();
        for (WebElement listingItem : listingItems) {
            JSONObject property = new JSONObject();

            String url = listingItem.getAttribute("href");
            String address = listingItem.findElement(By.className("css-17o293g")).getText();

            List<WebElement> prices = listingItem.findElements(By.className("css-s8wpzb"));

            ArrayList<String> allPrices = new ArrayList<String>();
            for (WebElement priceElement : prices) {
                allPrices.add(priceElement.getText());
            }

            property.put("address", address);
            property.put("url", url);
            property.put("propertyPrice", allPrices.get(0));
            property.put("propertyPricePerSquareMeter", allPrices.get(1));
            property.put("numberOfRooms", allPrices.get(2));
            property.put("propertySquareMeters", allPrices.get(3));
            allProperties.add(property);
        }

        try {
            FileWriter file = new FileWriter("propertiesList.json");
            file.write(allProperties.toJSONString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}











