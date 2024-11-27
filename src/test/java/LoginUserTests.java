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
import static org.junit.Assert.assertTrue;

public class LoginUserTests {

    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Пользователь может авторизоваться")
    public void shouldAuthorizeUserWithValidCredentials() {
        User user = createRandomUser();
        Response response = createUser(user, SC_OK);
        Response loginResponse = loginUser(user);
        verifySuccessfulLogin(loginResponse);
        accessToken = loginResponse.jsonPath().getString("accessToken");
        user.setToken(accessToken);
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


    @Test
    @DisplayName("Пользователь не может авторизоваться без пароля")
    public void shouldNotAuthorizeUserWithoutPassword() {
        User user = createRandomUserWithoutPassword();
        Response loginResponse = attemptToLoginWithoutPassword(user);
        verifyBadRequestResponseWithoutPassword(loginResponse);
    }

    @Step("Создаем случайного пользователя без пароля")
    private User createRandomUserWithoutPassword() {
        User user = randomUser();
        user.withEmptyPassword();
        return user;
    }

    @Step("Пытаемся авторизоваться пользователем без пароля")
    private Response attemptToLoginWithoutPassword(User user) {
        return userClient.login(user);
    }

    @Step("Проверяем, что ответ содержит ошибку о недостаточных данных для входа")
    private void verifyBadRequestResponseWithoutPassword(Response loginResponse) {
        assertEquals("Неверный статус код", SC_UNAUTHORIZED, loginResponse.statusCode());
        assertTrue(loginResponse.getBody().asString().contains("email or password are incorrect"));
    }


    @Test
    @DisplayName("Пользователь не может авторизоваться без логина")
    public void shouldNotAuthorizeCourierWithoutLogin() {
        User user = createRandomUserWithoutEmail();
        Response loginResponse = attemptToLoginWithoutEmail(user);
        verifyBadRequestResponseWithoutLogin(loginResponse);
    }

    @Step("Создаем случайного пользователя без логина")
    private User createRandomUserWithoutEmail() {
        User user = randomUser();
        user.setEmail(null);
        return user;
    }

    @Step("Пытаемся авторизоваться пользователем без логина")
    private Response attemptToLoginWithoutEmail(User user) {
        return userClient.login(user);
    }

    @Step("Проверяем, что ответ содержит ошибку о недостаточных данных для входа")
    private void verifyBadRequestResponseWithoutLogin(Response loginResponse) {
        assertEquals("Неверный статус код", SC_UNAUTHORIZED, loginResponse.statusCode());
        assertTrue(loginResponse.getBody().asString().contains("email or password are incorrect"));
    }


    @Test
    @DisplayName("Система вернёт ошибку, если неправильно указать логин")
    public void shouldReturnErrorForWrongLogin() {
        User user = createAndRegisterUser();
        User wrongPasswordUser = createUserWithWrongEmail(user);
        Response loginResponse = attemptToLoginWithWrongEmail(wrongPasswordUser);
        verifyErrorResponseForWrongEmail(loginResponse);
    }

    @Step("Создаем и регистрируем пользователя")
    private User createAndRegisterUser() {
        User user = randomUser();
        userClient.create(user);
        return user;
    }

    @Step("Создаем пользователя с неправильным email")
    private User createUserWithWrongEmail(User user) {
        return new User()
                .withEmail("wrongEmail")
                .withPassword(user.getPassword());
    }

    @Step("Пытаемся авторизоваться с неправильным email")
    private Response attemptToLoginWithWrongEmail(User user) {
        return userClient.login(user);
    }

    @Step("Проверяем, что ответ содержит ошибку для неправильного email")
    private void verifyErrorResponseForWrongEmail(Response loginResponse) {
        assertEquals("Неверный статус код", SC_UNAUTHORIZED, loginResponse.statusCode());
        assertTrue(loginResponse.getBody().asString().contains("email or password are incorrect"));
    }


    @Test
    @DisplayName("Система вернёт ошибку, если неправильно указать пароль")
    public void shouldReturnErrorForWrongPassword() {
        User user = createAndRegisterUserForWrongPassword();
        User wrongPasswordUser = createUserWithWrongPassword(user);
        Response loginResponse = attemptToLoginWrongPassword(wrongPasswordUser);
        verifyErrorResponseForWrongPassword(loginResponse);
    }

    @Step("Создаем и регистрируем пользователя")
    private User createAndRegisterUserForWrongPassword() {
        User user = randomUser();
        userClient.create(user);
        return user;
    }

    @Step("Создаем пользователя с неправильным паролем")
    private User createUserWithWrongPassword(User user) {
        return new User()
                .withEmail(user.getEmail())
                .withPassword("wrongPassword");
    }

    @Step("Пытаемся авторизоваться с неправильным паролем")
    private Response attemptToLoginWrongPassword(User user) {
        return userClient.login(user);
    }

    @Step("Проверяем, что ответ содержит ошибку для неправильного пароля")
    private void verifyErrorResponseForWrongPassword(Response loginResponse) {
        assertEquals("Неверный статус код", SC_UNAUTHORIZED, loginResponse.statusCode());
        assertTrue(loginResponse.getBody().asString().contains("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}
