package diplom2;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static diplom2.UserCreds.credsFromUser;
import static diplom2.UserGenerator.randomUser;

public class UserClient {

    private static final String AUTH_REGISTER_ENDPOINT = "/api/auth/register";
    private static final String AUTH_LOGIN_ENDPOINT = "/api/auth/login";
    private static final String AUTH_USER_ENDPOINT = "/api/auth/user/";
    private String accessToken;

    @Step("Создание и авторизация пользователя")
    public User createAndLoginUser() {
        User user = randomUser();
        create(user);
        login(user);
        return user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @Step("Создание пользователя")
    public Response create(User user) {
        return RestClient.getRequestSpecification()
                .body(user)
                .when()
                .post(AUTH_REGISTER_ENDPOINT);
    }

    @Step("Авторизация пользователя")
    public Response login(User user) {
        Response loginResponse = RestClient.getRequestSpecification()
                .body(credsFromUser(user))
                .when()
                .post(AUTH_LOGIN_ENDPOINT);
        accessToken = loginResponse.jsonPath().getString("accessToken");  // Сохраняем токен
        return loginResponse;
    }

    @Step("Удаление пользователя")
    public Response delete(String accessToken) {
        return RestClient.getRequestSpecification()
                .header("Authorization", accessToken)
                .when()
                .delete(AUTH_USER_ENDPOINT);
    }

    @Step("Изменение данных пользователя")
    public Response change(String accessToken, User updatedUser) {
        return RestClient.getRequestSpecification()
                .header("Authorization", accessToken)
                .body(updatedUser)
                .when()
                .patch(AUTH_USER_ENDPOINT);
    }

    @Step("Изменение данных пользователя")
    public Response change(User updatedUser) {
        return RestClient.getRequestSpecification()
                .body(updatedUser)
                .when()
                .patch(AUTH_USER_ENDPOINT);
    }
}