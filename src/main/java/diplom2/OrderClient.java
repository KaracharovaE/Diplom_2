package diplom2;

import io.qameta.allure.Step;
import io.restassured.response.Response;

public class OrderClient {

    private static final String ORDERS_ENDPOINT = "/api/orders";
    private static final String INGREDIENTS_ENDPOINT = "/api/ingredients";

    @Step("Создание заказа")
    public static Order createOrder(String[] ingredientIds) {
        Order order = new Order();
        order.setIngredients(ingredientIds);
        return order;
    }

    public Response createOrder(Order order) {
        return RestClient.getRequestSpecification()
                .body(order)
                .when()
                .post(ORDERS_ENDPOINT);
    }

    @Step("Получение заказа")
    public Response getOrder(String accessToken) {
        return RestClient.getRequestSpecification()
                .header("Authorization", accessToken)
                .when()
                .get(ORDERS_ENDPOINT);
    }

    @Step("Получение заказа без авторизации")
    public Response getOrderWithoutAuth() {
        return RestClient.getRequestSpecification()
                .when()
                .get(ORDERS_ENDPOINT);
    }

    @Step("Получение данных об ингредиентах")
    public Response getIngredients() {
        return RestClient.getRequestSpecification()
                .when()
                .get(INGREDIENTS_ENDPOINT);
    }
}
