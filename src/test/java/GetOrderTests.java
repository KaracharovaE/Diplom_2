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

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetOrderTests {

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
    @DisplayName("Получение заказов конкретного авторизованного пользователя")
    public void getOrderWithAuthorization() {
        User user = userClient.createAndLoginUser();
        accessToken = userClient.getAccessToken();

        Order order = OrderClient.createOrder(validIngredientIds.toArray(new String[0]));
        Response createOrderResponse = orderClient.createOrder(order);
        verifySuccessfulOrderCreation(createOrderResponse);

        Response getOrderResponse = orderClient.getOrder(accessToken);
        verifySuccessfulGetOrder(getOrderResponse);
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

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    public void getOrderWithoutAuthorization() {
        Order order = OrderClient.createOrder(validIngredientIds.toArray(new String[0]));
        Response createOrderResponse = orderClient.createOrder(order);
        verifySuccessfulOrderCreation(createOrderResponse);

        Response getOrderResponse = orderClient.getOrderWithoutAuth();
        verifyNotSuccessfulGetOrder(getOrderResponse);
    }

    @Step("Проверяем успешное создание заказа")
    private void verifySuccessfulOrderCreation(Response response) {
        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("success"));
    }

    @Step("Проверяем успешное получение заказа")
    private void verifySuccessfulGetOrder(Response response) {
        assertEquals("Неверный статус код", SC_OK, response.statusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("true"));
    }

    @Step("Проверяем неуспешное получение заказа")
    private void verifyNotSuccessfulGetOrder(Response response) {
        assertEquals("Неверный статус код", SC_UNAUTHORIZED, response.statusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("You should be authorised"));
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}
