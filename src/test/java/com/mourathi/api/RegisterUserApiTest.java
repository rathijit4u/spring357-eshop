package com.mourathi.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterUserApiTest {

    private static final String DATA_FILE = "src/test/data/customer_input_100.json";
    private static final String REGISTER_ENDPOINT = "/api/auth/register";
    private static final String TEST_ID_HEADER = "X-TEST-ID";
    private static final String TEST_ID_PREFIX = "eshop_user_register_";

    private static final String SUCCESS_MESSAGE = "User registered successfully";
    private static final String VALID_PASSWORD = "Photon$Blast10";

    // Expected status codes. Adjust these if your API uses different ones.
    private static final int CREATED = 201;
    private static final int BAD_REQUEST = 400;
    private static final int METHOD_NOT_ALLOWED = 405;
    private static final int CONFLICT = 409;
    private static final int UNSUPPORTED_MEDIA_TYPE = 415;

    private static final List<String> REQUIRED_FIELDS =
            List.of("firstName", "lastName", "username", "password", "email");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = System.getProperty("base_url", "http://localhost:8080");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static String testId(TestInfo testInfo) {
        return TEST_ID_PREFIX + testInfo.getTestMethod()
                .map(java.lang.reflect.Method::getName)
                .orElse(testInfo.getDisplayName());
    }

    public static List<String> loadCustomerIpPayloads() throws IOException {
        JsonNode root = MAPPER.readTree(new File(DATA_FILE));

        List<String> examples = new ArrayList<>();
        for (JsonNode node : root) {
            examples.add(MAPPER.writeValueAsString(node));
        }
        return examples;
    }

    static Stream<String> customerPayloads() throws IOException {
        return loadCustomerIpPayloads().stream();
    }

    private static String randomSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private static String randomPhone() {
        return "9" + String.format("%09d", ThreadLocalRandom.current().nextLong(1_000_000_000L));
    }

    /** A valid user with unique username, email and phone, so tests can be re-run safely. */
    private static ObjectNode validUser() {
        String s = randomSuffix();
        ObjectNode user = MAPPER.createObjectNode();
        user.put("firstName", "Carol");
        user.put("lastName", "Danvers");
        user.put("username", "user" + s);
        user.put("password", VALID_PASSWORD);
        user.put("email", "user" + s + "@example.com");
        user.put("phone", randomPhone());
        return user;
    }

    /** Makes username, email and phone unique, keeping the rest of the payload. */
    private static void makeUnique(ObjectNode body) {
        String s = randomSuffix();
        body.put("username", body.get("username").asText() + s.substring(0, 4));
        String[] email = body.get("email").asText().split("@", 2);
        body.put("email", email[0] + s + "@" + email[1]);
        body.put("phone", randomPhone());
    }

    private static RequestSpecification request(TestInfo testInfo) {
        return given().header(TEST_ID_HEADER, testId(testInfo));
    }

    private static Response register(ObjectNode body, TestInfo testInfo) {
        return register(body.toString(), testInfo);
    }

    private static Response register(String rawBody, TestInfo testInfo) {
        return request(testInfo)
                .contentType(ContentType.JSON)
                .body(rawBody)
                .when()
                .post(REGISTER_ENDPOINT);
    }

    // ------------------------------------------------------------------
    // Happy path
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Valid user return 201 and success message")
    void registerValidUser_returnsSuccess(TestInfo testInfo) throws IOException {
        List<String> customerIpPayloads = loadCustomerIpPayloads();

        Assert.isTrue(!customerIpPayloads.isEmpty(), "No customer input payload found!!!");

        ObjectNode body = (ObjectNode) MAPPER.readTree(customerIpPayloads.getFirst());
        makeUnique(body);

        Response response = register(body, testInfo)
                .then()
                .statusCode(CREATED)
                .contentType(ContentType.JSON)
                .body("message", equalTo(SUCCESS_MESSAGE))
                .extract()
                .response();

        assertEquals(CREATED, response.getStatusCode());
        assertEquals(SUCCESS_MESSAGE, response.jsonPath().getString("message"));
    }

    @Disabled("Only used for data loading")
    @ParameterizedTest(name = "[{index}] payload from file registers successfully")
    @MethodSource("customerPayloads")
    @DisplayName("All payloads from the data file register successfully")
    void registerAllPayloadsFromFile_returnsSuccess(String payload, TestInfo testInfo) throws IOException {
        ObjectNode body = (ObjectNode) MAPPER.readTree(payload);
        makeUnique(body);

        register(body, testInfo)
                .then()
                .statusCode(CREATED)
                .body("message", equalTo(SUCCESS_MESSAGE));
    }

    @Test
    @DisplayName("Names with unicode characters are accepted")
    void registerUserWithUnicodeNames_returnsSuccess(TestInfo testInfo) {
        ObjectNode body = validUser();
        body.put("firstName", "José");
        body.put("lastName", "Müller");

        register(body, testInfo).then().statusCode(CREATED);
    }

    @Test
    @DisplayName("Unknown extra fields are ignored")
    void registerUserWithExtraFields_isIgnored(TestInfo testInfo) {
        ObjectNode body = validUser();
        body.put("role", "ADMIN");
        body.put("unexpectedField", "value");

        register(body, testInfo).then().statusCode(CREATED);
    }

    @Test
    @DisplayName("Response never exposes the password")
    void registerValidUser_doesNotLeakPassword(TestInfo testInfo) {
        register(validUser(), testInfo)
                .then()
                .statusCode(CREATED)
                .body("password", nullValue())
                .body(not(containsString(VALID_PASSWORD)));
    }

    // ------------------------------------------------------------------
    // Duplicates
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Duplicate username returns 409")
    void registerDuplicateUsername_returnsConflict(TestInfo testInfo) {
        ObjectNode first = validUser();
        register(first, testInfo).then().statusCode(CREATED);

        ObjectNode second = validUser();
        second.put("username", first.get("username").asText());

        register(second, testInfo).then().statusCode(CONFLICT);
    }

    @Test
    @DisplayName("Duplicate email returns 409")
    void registerDuplicateEmail_returnsConflict(TestInfo testInfo) {
        ObjectNode first = validUser();
        register(first, testInfo).then().statusCode(CREATED);

        ObjectNode second = validUser();
        second.put("email", first.get("email").asText());

        register(second, testInfo).then().statusCode(CONFLICT);
    }

    @Test
    @DisplayName("Duplicate phone returns 409")
    @Disabled
    void registerDuplicatePhone_returnsConflict(TestInfo testInfo) {
        ObjectNode first = validUser();
        register(first, testInfo).then().statusCode(CREATED);

        ObjectNode second = validUser();
        second.put("phone", first.get("phone").asText());

        register(second, testInfo).then().statusCode(CONFLICT);
    }

    @Test
    @DisplayName("Registering the exact same payload twice returns 409 the second time")
    void registerSamePayloadTwice_returnsConflict(TestInfo testInfo) {
        ObjectNode body = validUser();

        register(body, testInfo).then().statusCode(CREATED);
        register(body, testInfo).then().statusCode(CONFLICT);
    }

    // ------------------------------------------------------------------
    // Missing / null / blank fields
    // ------------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] missing {0} returns 400")
    @ValueSource(strings = {"firstName", "lastName", "username", "password", "email"})
    @DisplayName("Missing required field returns 400")
    void registerMissingField_returnsBadRequest(String field, TestInfo testInfo) {
        ObjectNode body = validUser();
        body.remove(field);

        register(body, testInfo).then().statusCode(BAD_REQUEST);
    }

    @ParameterizedTest(name = "[{index}] null {0} returns 400")
    @ValueSource(strings = {"firstName", "lastName", "username", "password", "email"})
    @DisplayName("Null required field returns 400")
    void registerNullField_returnsBadRequest(String field, TestInfo testInfo) {
        ObjectNode body = validUser();
        body.putNull(field);

        register(body, testInfo).then().statusCode(BAD_REQUEST);
    }

    static Stream<Arguments> blankFieldCases() {
        return REQUIRED_FIELDS.stream()
                .flatMap(field -> Stream.of("", "   ").map(value -> Arguments.of(field, value)));
    }

    @ParameterizedTest(name = "[{index}] {0} = ''{1}'' returns 400")
    @MethodSource("blankFieldCases")
    @DisplayName("Blank or whitespace-only required field returns 400")
    void registerBlankField_returnsBadRequest(String field, String value, TestInfo testInfo) {
        ObjectNode body = validUser();
        body.put(field, value);

        register(body, testInfo).then().statusCode(BAD_REQUEST);
    }

    // ------------------------------------------------------------------
    // Field format validation
    // ------------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] invalid email ''{0}'' returns 400")
    @ValueSource(strings = {
            "plainaddress",
            "@no-local-part.com",
            "user@",
            "user@@example.com",
            "user example@example.com",
            "user@.com"
    })
    @DisplayName("Invalid email format returns 400")
    void registerInvalidEmail_returnsBadRequest(String email, TestInfo testInfo) {
        ObjectNode body = validUser();
        body.put("email", email);

        register(body, testInfo).then().statusCode(BAD_REQUEST);
    }

    @ParameterizedTest(name = "[{index}] invalid phone ''{0}'' returns 400")
    @ValueSource(strings = {
            "12345",
            "98765000100000",
            "abcdefghij",
            "98765-00010"
    })
    @DisplayName("Invalid phone number returns 400")
    void registerInvalidPhone_returnsBadRequest(String phone, TestInfo testInfo) {
        ObjectNode body = validUser();
        body.put("phone", phone);

        register(body, testInfo).then().statusCode(BAD_REQUEST);
    }

    @ParameterizedTest(name = "[{index}] weak password ''{0}'' returns 400")
    @ValueSource(strings = {
            "Sh0rt!",             // too short
            "alllowercase1!",     // no uppercase
            "ALLUPPERCASE1!",     // no lowercase
            "NoDigits!!Here",     // no digit
            "NoSpecial123Abc"     // no special character
    })
    @DisplayName("Weak password returns 400")
    void registerWeakPassword_returnsBadRequest(String password, TestInfo testInfo) {
        ObjectNode body = validUser();
        body.put("password", password);

        register(body, testInfo).then().statusCode(BAD_REQUEST);
    }

    // ------------------------------------------------------------------
    // Robustness: odd input should never cause a 500
    // ------------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] hostile firstName does not cause a server error")
    @ValueSource(strings = {
            "'; DROP TABLE users;--",
            "<script>alert(1)</script>",
            "${jndi:ldap://example.com/a}",
            "Robert'); DROP TABLE Students;--"
    })
    @DisplayName("SQL/script injection strings are handled safely")
    void registerInjectionStrings_doNotCauseServerError(String value, TestInfo testInfo) {
        ObjectNode body = validUser();
        body.put("firstName", value);

        register(body, testInfo)
                .then()
                .statusCode(anyOf(is(CREATED), is(BAD_REQUEST)));
    }

    @Test
    @DisplayName("Very long field values do not cause a server error")
    void registerVeryLongValues_doNotCauseServerError(TestInfo testInfo) {
        ObjectNode body = validUser();
        body.put("firstName", "a".repeat(1000));
        body.put("lastName", "b".repeat(1000));

        register(body, testInfo).then().statusCode(lessThan(500));
    }

    // ------------------------------------------------------------------
    // Request-level errors
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Malformed JSON returns 400")
    void registerMalformedJson_returnsBadRequest(TestInfo testInfo) {
        register("{ \"firstName\": \"Carol\", ", testInfo).then().statusCode(BAD_REQUEST);
    }

    @Test
    @DisplayName("Empty body returns 400")
    void registerEmptyBody_returnsBadRequest(TestInfo testInfo) {
        request(testInfo)
                .contentType(ContentType.JSON)
                .when()
                .post(REGISTER_ENDPOINT)
                .then()
                .statusCode(BAD_REQUEST);
    }

    @Test
    @DisplayName("Empty JSON object returns 400")
    void registerEmptyObject_returnsBadRequest(TestInfo testInfo) {
        register("{}", testInfo).then().statusCode(BAD_REQUEST);
    }

    @Test
    @DisplayName("JSON array instead of object returns 400")
    void registerJsonArray_returnsBadRequest(TestInfo testInfo) {
        register("[" + validUser() + "]", testInfo).then().statusCode(BAD_REQUEST);
    }

    @Test
    @DisplayName("Non-JSON content type returns 415")
    void registerWrongContentType_returnsUnsupportedMediaType(TestInfo testInfo) {
        request(testInfo)
                .contentType(ContentType.TEXT)
                .body(validUser().toString())
                .when()
                .post(REGISTER_ENDPOINT)
                .then()
                .statusCode(UNSUPPORTED_MEDIA_TYPE);
    }

    @ParameterizedTest(name = "[{index}] {0} returns 405")
    @ValueSource(strings = {"GET", "PUT", "PATCH", "DELETE"})
    @DisplayName("Unsupported HTTP methods return 405")
    void registerWrongMethod_returnsMethodNotAllowed(String method, TestInfo testInfo) {
        request(testInfo)
                .contentType(ContentType.JSON)
                .body(validUser().toString())
                .when()
                .request(method, REGISTER_ENDPOINT)
                .then()
                .statusCode(METHOD_NOT_ALLOWED);
    }
}
