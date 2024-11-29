import diplom2.Order;
import diplom2.OrderClient;
import diplom2.User;
import diplom2.UserClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateOrderTests {

    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;
    private List<String> validIngredientIds;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        Response ingredientsResponse = orderClient.getIngredients();
        verifySuccessfulGetIngredients(ingredientsResponse);
        validIngredientIds = extractIngredientIds(ingredientsResponse);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuthorization() {
        User user = userClient.createAndLoginUser();
        accessToken = userClient.getAccessToken();

        Order order = OrderClient.createOrder(validIngredientIds.toArray(new String[0]));
        Response response = orderClient.createOrder(order);
        verifySuccessfulOrderCreation(response);
    }

    @Step("Проверяем успешное получение ингредиентов")
    private void verifySuccessfulGetIngredients(Response response) {
        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("true"));
    }

    private List<String> extractIngredientIds(Response response) {
        return response.jsonPath().getList("data._id");
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
        Order order = OrderClient.createOrder(validIngredientIds.toArray(new String[0]));
        Response response = orderClient.createOrder(order);
        verifyBadRequestResponse(response, SC_BAD_REQUEST);
    }

    @Step("Проверяем сообщение об ошибке")
    private void verifyBadRequestResponse(Response response, int expectedStatusCode) {
        assertEquals("Неверный статус код", expectedStatusCode, response.statusCode());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    public void createOrderWithIngredients() {
        User user = userClient.createAndLoginUser();
        accessToken = userClient.getAccessToken();

        Order order = OrderClient.createOrder(validIngredientIds.toArray(new String[0]));
        Response response = orderClient.createOrder(order);
        verifySuccessfulOrderCreation(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        User user = userClient.createAndLoginUser();
        accessToken = userClient.getAccessToken();

        Order order = OrderClient.createOrder(new String[]{});
        Response response = orderClient.createOrder(order);
        verifyBadRequestResponse(response, SC_BAD_REQUEST);
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientHash() {
        User user = userClient.createAndLoginUser();
        accessToken = userClient.getAccessToken();

        String[] invalidIngredientIds = new String[]{"0123"};
        Order order = OrderClient.createOrder(invalidIngredientIds);
        Response response = orderClient.createOrder(order);
        verifyBadRequestResponse(response, SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}