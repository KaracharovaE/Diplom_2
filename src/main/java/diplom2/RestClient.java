package diplom2;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class RestClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    public static RequestSpecification getRequestSpecification() {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json");
    }
}