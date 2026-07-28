package com.mourathi;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginApiTest {

    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String TEST_ID_HEADER = "X-TEST-ID";
    private static final String TEST_ID_PREFIX = "eshop_login_";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = System.getProperty("base_url", "http://localhost:8080");
    }

    private static String testId(TestInfo testInfo) {
        return TEST_ID_PREFIX + testInfo.getTestMethod()
                .map(java.lang.reflect.Method::getName)
                .orElse(testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Valid credentials return 200 and success message")
    void loginWithValidCredentials_returnsSuccess(TestInfo testInfo) {
        String requestBody = """
                {
                  "username": "bbanner_sci",
                  "password": "GammaRay@2026"
                }
                """;

        Response response = given()
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(requestBody)
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("message", equalTo("Login successful"))
                .extract()
                .response();

        assertEquals(200, response.getStatusCode());
        assertEquals("Login successful", response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Wrong password returns non-200 status")
    void loginWithWrongPassword_returnsFailure(TestInfo testInfo) {
        String requestBody = """
                {
                  "username": "bbanner_sci",
                  "password": "WrongPassword123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(requestBody)
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .statusCode(org.hamcrest.Matchers.equalTo(401));
    }

    @Test
    @DisplayName("Unknown username returns 401 status")
    void loginWithUnknownUsername_returnsFailure(TestInfo testInfo) {
        String requestBody = """
                {
                  "username": "does_not_exist",
                  "password": "GammaRay@2026"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(requestBody)
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .statusCode(org.hamcrest.Matchers.equalTo(401));
    }

    @Test
    @DisplayName("Missing password field returns 400 status")
    void loginWithMissingPassword_returnsFailure(TestInfo testInfo) {
        String requestBody = """
                {
                  "username": "bbanner_sci"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(requestBody)
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .statusCode(org.hamcrest.Matchers.equalTo(400));
    }

    @Test
    @DisplayName("Empty request body returns non-200 status")
    void loginWithEmptyBody_returnsFailure(TestInfo testInfo) {
        given()
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body("{}")
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .statusCode(org.hamcrest.Matchers.equalTo(400));
    }

    @Test
    @DisplayName("Response time is within acceptable threshold")
    void loginResponse_isWithinTimeLimit(TestInfo testInfo) {
        String requestBody = """
                {
                  "username": "bbanner_sci",
                  "password": "GammaRay@2026"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(requestBody)
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .time(org.hamcrest.Matchers.lessThan(2000L));
    }
}
