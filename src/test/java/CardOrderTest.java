

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
        // Открытие страницы вынесено в метод setup как предусловие тестов
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldSubmitFormWithValidData() {

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

        // Заполняем форму, но не ставим галочку
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79211234567");
        // Галочку не ставим
        driver.findElement(By.cssSelector("button.button")).click();

        // Проверяем, что чекбокс выделен красным с использованием комбинированного селектора
        WebElement agreementCheckbox = driver.findElement(By.cssSelector("[data-test-id=agreement].input_invalid"));
        assertTrue(agreementCheckbox.isDisplayed());
    }

    @Test
    void shouldShowErrorWithEmptyName() {

        // Оставляем имя пустым
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79211234567");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        String errorText = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText();
        assertTrue(errorText.contains("Поле обязательно для заполнения"));
    }

    @Test
    void shouldShowErrorWithEmptyPhone() {

        // Оставляем телефон пустым
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        String errorText = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText();
        assertTrue(errorText.contains("Поле обязательно для заполнения"));
    }
}