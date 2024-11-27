package diplom2;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient {

    public Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/orders");
    }

    public Response getOrder(String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .get("/api/orders");
    }

    public Response getOrderWithoutAuth() {
        return given()
                .header("Content-type", "application/json")
                .get("/api/orders");
    }
}
