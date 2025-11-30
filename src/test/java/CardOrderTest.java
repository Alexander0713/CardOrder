

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardOrderTest {
    private WebDriver driver;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldSubmitFormWithValidData() {
        driver.get("http://localhost:9999");

        // Заполняем поле "Фамилия и имя"
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");

        // Заполняем поле "Телефон"
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79211234567");

        // Ставим галочку согласия
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();

        // Нажимаем кнопку "Продолжить"
        driver.findElement(By.cssSelector("button.button")).click();

        // Проверяем сообщение об успехе
        String successText = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText().trim();
        assertTrue(successText.contains("Ваша заявка успешно отправлена"));
    }

    @Test
    void shouldShowErrorWithInvalidName() {
        driver.get("http://localhost:9999");

        // Заполняем поле "Фамилия и имя" латинскими буквами
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Ivanov Ivan");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79211234567");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        // Проверяем сообщение об ошибке
        String errorText = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText();
        assertTrue(errorText.contains("Имя и Фамилия указаные неверно"));
    }

    @Test
    void shouldShowErrorWithInvalidPhone() {
        driver.get("http://localhost:9999");

        // Заполняем поле "Телефон" неверным форматом
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("89211234567"); // без +
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        // Проверяем сообщение об ошибке
        String errorText = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText();
        assertTrue(errorText.contains("Телефон указан неверно"));
    }

    @Test
    void shouldShowErrorWithoutAgreement() {
        driver.get("http://localhost:9999");

        // Заполняем форму, но не ставим галочку
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79211234567");
        // Галочку не ставим
        driver.findElement(By.cssSelector("button.button")).click();

        // Проверяем, что чекбокс выделен красным
        WebElement agreementCheckbox = driver.findElement(By.cssSelector("[data-test-id=agreement]"));
        assertTrue(agreementCheckbox.getAttribute("class").contains("input_invalid"));
    }

    @Test
    void shouldShowErrorWithEmptyName() {
        driver.get("http://localhost:9999");

        // Оставляем имя пустым
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79211234567");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        String errorText = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText();
        assertTrue(errorText.contains("Поле обязательно для заполнения"));
    }

    @Test
    void shouldShowErrorWithEmptyPhone() {
        driver.get("http://localhost:9999");

        // Оставляем телефон пустым
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        String errorText = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText();
        assertTrue(errorText.contains("Поле обязательно для заполнения"));
    }
}