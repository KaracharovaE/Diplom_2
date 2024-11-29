import diplom2.User;
import diplom2.UserClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static diplom2.UserGenerator.randomUser;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateUserTests {

    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Создание пользователя")
    public void createUser() {
        User user = randomUser();
        Response response = createUserAndAssert(user, SC_OK);
        verifyResponseContainsTrue(response);
        Response loginResponse = userClient.login(user);
        assertEquals("Не удалось авторизовать пользователя", SC_OK, loginResponse.statusCode());
        accessToken = loginResponse.jsonPath().getString("accessToken");
    }

    @Step("Создаем пользователя и проверяем статус")
    private Response createUserAndAssert(User user, int expectedStatusCode) {
        Response response = userClient.create(user);
        assertEquals("Неверный статус код", expectedStatusCode, response.statusCode()); //посмотреть
        return response;
    }

    @Step("Проверяем, что ответ содержит 'true'")
    private void verifyResponseContainsTrue(Response response) {
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("true"));
    }

    @Test
    @DisplayName("Создание двух идентичных пользователей")
    public void createTwoIdenticalUsers() {
        User user = randomUser();
        Response firstResponse = createUserAndAssert(user, SC_OK);
        Response loginResponse = userClient.login(user);
        Response secondResponse = createUserAndAssert(user, SC_FORBIDDEN);
        verifyConflictResponse(secondResponse);
    }

    @Step("Проверяем сообщение об ошибке")
    private void verifyConflictResponse(Response response) {
        assertEquals("Неверный статус код", SC_FORBIDDEN, response.statusCode());
        assertTrue(response.getBody().asString().contains("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void createUserWithoutLogin() {
        User user = createRandomUserWithMissingField(null, "password", "name");
        Response response = userClient.create(user);
        verifyBadRequestResponse(response, "Email, password and name are required fields");
    }

    @Step("Создаем случайного пользователя без email или пароля или имени")
    private User createRandomUserWithMissingField(String email, String password, String name) {
        User user = randomUser();
        if (email == null) {
            user.setEmail(null);
        }
        if (password == null) {
            user.setPassword(null);
        }
        if (name == null) {
            user.setPassword(null);
        }
        return user;
    }

    @Step("Проверяем сообщение об ошибке")
    private void verifyBadRequestResponse(Response response, String expectedMessage) {
        assertEquals("Неверный статус код", SC_FORBIDDEN, response.statusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains(expectedMessage));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void createCourierWithoutPassword() {
        User user = createRandomUserWithMissingField("email", null, "name");
        Response response = userClient.create(user);
        verifyBadRequestResponse(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    public void createUserWithoutName() {
        User user = createRandomUserWithMissingField("email", "password", null);
        Response response = userClient.create(user);
        verifyBadRequestResponse(response, "Email, password and name are required fields");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}



