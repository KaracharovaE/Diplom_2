import diplom2.User;
import diplom2.UserClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static diplom2.UserGenerator.randomUser;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class ChangesUserTests {

    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Пользователь после авторизации может изменить имя")
    public void userCanChangeNameAfterAuthorization() {
        User user = createRandomUser();
        Response response = createUser(user, SC_OK);
        Response loginResponse = loginUser(user);
        verifySuccessfulLogin(loginResponse);
        accessToken = loginResponse.jsonPath().getString("accessToken");
        user.setToken(accessToken);
        user.setName("Новое Имя");
        Response changeResponse = userClient.change(accessToken, user);
        assertEquals("Неверный статус код при изменении имени", SC_OK, changeResponse.statusCode());
    }

    @Step("Создаем случайного пользователя")
    private User createRandomUser() {
        return randomUser();
    }

    @Step("Создаем пользователя и проверяем статус")
    private Response createUser(User user, int expectedStatusCode) {
        Response response = userClient.create(user);
        assertEquals("Неверный статус код", expectedStatusCode, response.statusCode());
        return response;
    }

    @Step("Авторизуем пользователя")
    private Response loginUser(User user) {
        Response loginResponse = userClient.login(user);
        return loginResponse;
    }

    @Step("Проверяем успешную авторизацию")
    private void verifySuccessfulLogin(Response loginResponse) {
        assertEquals("Неверный статус код", SC_OK, loginResponse.statusCode());
    }

    @Step("Изменяем имя пользователя")
    private void changesName(String newName) {
        User user = new User();
        user.setName(newName);
        Response response = userClient.change(accessToken, user);
        assertEquals("Неверный статус код при изменении имени", SC_OK, response.statusCode());
    }

    @Test
    @DisplayName("Пользователь после авторизации может изменить email")
    public void userCanChangeEmailAfterAuthorization() {
        User user = createRandomUser();
        Response response = createUser(user, SC_OK);
        Response loginResponse = loginUser(user);
        verifySuccessfulLogin(loginResponse);
        accessToken = loginResponse.jsonPath().getString("accessToken");
        user.setToken(accessToken);

        user.setEmail("newemail123@ya.ru");
        Response changeResponse = userClient.change(accessToken, user);
        assertEquals("Неверный статус код при изменении email", SC_OK, changeResponse.statusCode());
    }

    @Step("Изменяем имя пользователя")
    private void changesEmail(String newEmail) {
        User user = new User();
        user.setName(newEmail);
        Response response = userClient.change(accessToken, user);
        assertEquals("Неверный статус код при изменении email", SC_OK, response.statusCode());
    }

    @Test
    @DisplayName("Пользователь без авторизации может изменить имя")
    public void userCanChangeNameWithoutAuthorization() {
        User user = createRandomUser();
        Response response = createUser(user, SC_OK);

        user.setName("Малина");
        Response changeResponse = userClient.change(user);
        assertEquals("Неверный статус код при изменении имени", SC_UNAUTHORIZED, changeResponse.statusCode());
    }

    @Test
    @DisplayName("Пользователь без авторизации может изменить email")
    public void userCanChangeEmailWithoutAuthorization() {
        User user = createRandomUser();
        Response response = createUser(user, SC_OK);

        user.setEmail("newemail12345@ya.ru");
        Response changeResponse = userClient.change(user);
        assertEquals("Неверный статус код при изменении email", SC_UNAUTHORIZED, changeResponse.statusCode());
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}
