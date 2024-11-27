package diplom2;

import io.restassured.response.Response;

import static diplom2.UserCreds.credsFromUser;
import static io.restassured.RestAssured.given;

public class UserClient {

    public Response create(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("api/auth/register");
    }

    public Response login(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(credsFromUser(user))
                .when()
                .post("api/auth/login");
    }

    public Response delete(String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .delete("api/auth/user/");
    }

    public Response change(String accessToken, User updatedUser) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(updatedUser)
                .when()
                .patch("api/auth/user");
    }

    public Response change(User updatedUser) {
        return given()
                .header("Content-type", "application/json")
                .body(updatedUser)
                .when()
                .patch("api/auth/user");
    }
}