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
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;

public class GetOrderTests {

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
    @DisplayName("Получение заказов конкретного авторизованного пользователя")
    public void getOrderWithAuthorization() {
        User user = createAndLoginUser();

        Order order = createOrder(new String[]{"61c0c5a71d1f82001bdaaa6d"});
        Response response = attemptToCreateOrder(order);
        verifySuccessfulOrderCreation(response);

        Response response1 = attemptToGetOrder();
        verifySuccessfulGetOrder(response1);
    }

    @Step("Создаем и авторизируем пользователя")
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

    @Step("Создаем заказ")
    private Order createOrder(String[] ingredientIds) {
        Order order = new Order();
        order.setIngredients(ingredientIds);
        return order;
    }

    @Step("Проверяем успешное создание заказа")
    private void verifySuccessfulOrderCreation(Response response) {
        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("success"));
    }

    @Step("Пытаемся получить заказ")
    private Response attemptToGetOrder() {
            return orderClient.getOrder(accessToken);
        }

    @Step("Проверяем успешное получение заказа")
    private void verifySuccessfulGetOrder(Response response) {
        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("true"));
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    public void createOrderWithoutAuthorization() {

        Order order = createOrder(new String[]{"61c0c5a71d1f82001bdaaa6d"});
        Response response = attemptToCreateOrder(order);
        verifySuccessfulOrderCreation(response);

        Response response1 = attemptToGetOrderWithoutAuthorization();
        verifyNotSuccessfulGetOrder(response1);
    }

    @Step("Пытаемся получить заказ без авторизации")
    private Response attemptToGetOrderWithoutAuthorization() {
        return orderClient.getOrderWithoutAuth();
    }

    @Step("Проверяем неуспешное получение заказа")
    private void verifyNotSuccessfulGetOrder(Response response) {
        assertEquals("Неверный статус код", SC_UNAUTHORIZED, response.statusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("You should be authorised")); //
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}

