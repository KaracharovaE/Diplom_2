import diplom2.Order;
import diplom2.OrderClient;
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
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateOrderTests {

    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuthorization() {
        User user = createAndLoginUser();
        Order order = createOrder(new String[]{"61c0c5a71d1f82001bdaaa6d"});
        Response response = attemptToCreateOrder(order);
        verifySuccessfulOrderCreation(response);
    }

    @Step("Создаем и авторизуем пользователя")
    private User createAndLoginUser() {
        User user = randomUser();
        userClient.create(user);
        Response loginResponse = userClient.login(user);
        accessToken = loginResponse.jsonPath().getString("accessToken");
        user.setToken(accessToken);
        return user;
    }

    @Step("Пытаемся создать заказ")
    private Response attemptToCreateOrder(Order order) {
        return orderClient.createOrder(order);
    }

    @Step("Проверяем успешное создание заказа")
    private void verifySuccessfulOrderCreation(Response response) {
        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("success"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthorization() {
        Order order = createOrder(new String[]{"61c0c5a71d1f82001bdaaa6d"});
        Response response = attemptToCreateOrder(order);
        verifyBadRequestResponse(response, SC_BAD_REQUEST);
    }

    @Step("Проверяем сообщение об ошибке")
    private void verifyBadRequestResponse(Response response, int expectedStatusCode) {
        assertEquals("Неверный статус код", expectedStatusCode, response.statusCode());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    public void createOrderWithIngredients() {
        User user = createAndLoginUser();
        Order order = createOrder(new String[]{"61c0c5a71d1f82001bdaaa6d"});
        Response response = attemptToCreateOrder(order);
        verifySuccessfulOrderCreation(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        User user = createAndLoginUser();
        Order order = createOrder(new String[]{});
        Response response = attemptToCreateOrder(order);
        verifyBadRequestResponse(response, SC_BAD_REQUEST);
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientHash() {
        User user = createAndLoginUser();
        Order order = createOrder(new String[]{"61c0c5a71d1f82001bd"});
        Response response = attemptToCreateOrder(order);
        verifyBadRequestResponse(response, SC_INTERNAL_SERVER_ERROR);
    }

    @Step("Создаем заказ")
    private Order createOrder(String[] ingredientIds) {
        Order order = new Order();
        order.setIngredients(ingredientIds);
        return order;
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}