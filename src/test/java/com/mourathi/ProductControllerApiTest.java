package com.mourathi;

import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductControllerApiTest {

    private static final String PRODUCTS_ENDPOINT = "/api/products";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String TEST_ID_HEADER = "X-TEST-ID";
    private static final String TEST_ID_PREFIX = "eshop_products_";
    private static final Set<String> usedProductIds = new HashSet<>();

    // Captures the JSESSIONID cookie from login and replays it on every
    // subsequent request that passes this same filter instance.
    private static final SessionFilter sessionFilter = new SessionFilter();

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = System.getProperty("base_url", "http://localhost:8080");

        String loginBody = """
                {
                  "username": "bbanner_sci",
                  "password": "GammaRay@2026"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .filter(sessionFilter)
                .body(loginBody)
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .statusCode(200);
    }

    private static String testId(TestInfo testInfo) {
        return TEST_ID_PREFIX + testInfo.getTestMethod()
                .map(java.lang.reflect.Method::getName)
                .orElse(testInfo.getDisplayName());
    }

    private static String validProductBody(String name, String category, int stockQuantity) {
        return """
                {
                  "name": "%s",
                  "description": "A high quality %s",
                  "price": 49.99,
                  "category": "%s",
                  "stockQuantity": %d,
                  "sku": "SKU-%s"
                }
                """.formatted(name, name, category, stockQuantity, UUID.randomUUID().toString().substring(0, 8));
    }

    // ---------- POST /api/products ----------

    @Test
    @DisplayName("Create product with valid payload returns 201 and product data")
    void createProduct_withValidPayload_returns201(TestInfo testInfo) {
        String requestBody = validProductBody("Wireless Mouse", "Electronics", 25);

        String productId = given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(requestBody)
                .when()
                .post(PRODUCTS_ENDPOINT)
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("success", equalTo(true))
                .body("message", equalTo("Product created successfully"))
                .body("data.id", notNullValue())
                .body("data.name", equalTo("Wireless Mouse"))
                .body("data.category", equalTo("Electronics"))
                .extract()
                .path("data.id");

        usedProductIds.add(productId);

    }

    @Test
    @DisplayName("Create product with missing required field returns 400")
    void createProduct_withMissingName_returns400(TestInfo testInfo) {
        String requestBody = """
                {
                  "description": "Missing the name field",
                  "price": 19.99,
                  "category": "Electronics",
                  "stockQuantity": 10,
                  "sku": "SKU-MISSING"
                }
                """;

        given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(requestBody)
                .when()
                .post(PRODUCTS_ENDPOINT)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Create product with negative price returns 400")
    void createProduct_withNegativePrice_returns400(TestInfo testInfo) {
        String requestBody = """
                {
                  "name": "Broken Priced Item",
                  "description": "Negative price should fail validation",
                  "price": -5.00,
                  "category": "Electronics",
                  "stockQuantity": 5,
                  "sku": "SKU-NEG"
                }
                """;

        given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(requestBody)
                .when()
                .post(PRODUCTS_ENDPOINT)
                .then()
                .statusCode(400);
    }

    // ---------- POST /api/products/bulk ----------

    @Test
    @DisplayName("Bulk create with valid payloads returns 201 and all products")
    void createProductBulk_withValidPayloads_returns201(TestInfo testInfo) {
        String requestBody = """
                [
                  {
                    "name": "Bulk Item One",
                    "description": "First bulk item",
                    "price": 9.99,
                    "category": "Accessories",
                    "stockQuantity": 15,
                    "sku": "SKU-BULK-1"
                  },
                  {
                    "name": "Bulk Item Two",
                    "description": "Second bulk item",
                    "price": 14.99,
                    "category": "Accessories",
                    "stockQuantity": 8,
                    "sku": "SKU-BULK-2"
                  }
                ]
                """;

        Response res = given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(requestBody)
                .when()
                .post(PRODUCTS_ENDPOINT + "/bulk")
                .then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("data.size()", equalTo(2))
                .body("data[0].name", equalTo("Bulk Item One"))
                .body("data[1].name", equalTo("Bulk Item Two"))
                .extract()
                .response();
        List<Map<String, String>> products = res.path("data");
        for(Map<String, String> product : products) {
            usedProductIds.add(product.get("id"));
        }
    }

    @Test
    @DisplayName("Bulk create with empty list returns 201 and empty data")
    void createProductBulk_withEmptyList_returns201WithEmptyData(TestInfo testInfo) {
        given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body("[]")
                .when()
                .post(PRODUCTS_ENDPOINT + "/bulk")
                .then()
                .statusCode(201)
                .body("data.size()", equalTo(0));
    }

    @Test
    @DisplayName("Bulk create with one invalid item returns 400")
    void createProductBulk_withOneInvalidItem_returns400(TestInfo testInfo) {
        String requestBody = """
                [
                  {
                    "name": "Valid Item",
                    "description": "This one is fine",
                    "price": 9.99,
                    "category": "Accessories",
                    "stockQuantity": 15,
                    "sku": "SKU-VALID"
                  },
                  {
                    "description": "Missing name, should fail validation",
                    "price": 14.99,
                    "category": "Accessories",
                    "stockQuantity": 8,
                    "sku": "SKU-INVALID"
                  }
                ]
                """;

        given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(requestBody)
                .when()
                .post(PRODUCTS_ENDPOINT + "/bulk")
                .then()
                .statusCode(400);
    }

    // ---------- GET /api/products/{id} ----------

    @Test
    @DisplayName("Get product by existing id returns 200 and product data")
    void getProductById_whenExists_returns200(TestInfo testInfo) {
        // Create a product first so we have a known id to fetch
        String createBody = validProductBody("Fetchable Widget", "Widgets", 12);

        String productId = given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(createBody)
                .when()
                .post(PRODUCTS_ENDPOINT)
                .then()
                .statusCode(201)
                .extract()
                .path("data.id");

        usedProductIds.add(productId);

        given()
                .filter(sessionFilter)
                .header(TEST_ID_HEADER, testId(testInfo))
                .when()
                .get(PRODUCTS_ENDPOINT + "/{id}", productId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.id", equalTo(productId))
                .body("data.name", equalTo("Fetchable Widget"));
    }

    @Test
    @DisplayName("Get product by non-existent id returns 404")
    void getProductById_whenNotFound_returns404(TestInfo testInfo) {
        UUID randomId = UUID.randomUUID();

        given()
                .filter(sessionFilter)
                .header(TEST_ID_HEADER, testId(testInfo))
                .when()
                .get(PRODUCTS_ENDPOINT + "/{id}", randomId)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Get product by malformed id returns 400")
    void getProductById_withMalformedId_returns400(TestInfo testInfo) {
        given()
                .filter(sessionFilter)
                .header(TEST_ID_HEADER, testId(testInfo))
                .when()
                .get(PRODUCTS_ENDPOINT + "/{id}", "not-a-uuid")
                .then()
                .statusCode(400);
    }

    // ---------- GET /api/products (paginated) ----------

    @Test
    @DisplayName("Get all products returns 200 with paginated response")
    void getAllProducts_returnsPagedResponse(TestInfo testInfo) {
        given()
                .filter(sessionFilter)
                .header(TEST_ID_HEADER, testId(testInfo))
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get(PRODUCTS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", notNullValue());
    }

    @Test
    @DisplayName("Get all products with sort param returns 200")
    void getAllProducts_withSort_returns200(TestInfo testInfo) {
        given()
                .filter(sessionFilter)
                .header(TEST_ID_HEADER, testId(testInfo))
                .queryParam("page", 0)
                .queryParam("size", 5)
                .queryParam("sort", "price,desc")
                .when()
                .get(PRODUCTS_ENDPOINT)
                .then()
                .statusCode(200);
    }

    // ---------- GET /api/products/in-stock ----------

    @Test
    @DisplayName("Get in-stock products returns 200 with paginated response")
    void getInStockProducts_returns200(TestInfo testInfo) {
        given()
                .filter(sessionFilter)
                .header(TEST_ID_HEADER, testId(testInfo))
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get(PRODUCTS_ENDPOINT + "/in-stock")
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    // ---------- GET /api/products/category/{category} ----------

    @Test
    @DisplayName("Get products by category returns 200 and matching items")
    void getByCategory_returns200(TestInfo testInfo) {
        // Ensure at least one product exists in this category
        String createBody = validProductBody("Category Test Item", "Books", 5);

        given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(createBody)
                .when()
                .post(PRODUCTS_ENDPOINT)
                .then()
                .statusCode(201);

        given()
                .filter(sessionFilter)
                .header(TEST_ID_HEADER, testId(testInfo))
                .when()
                .get(PRODUCTS_ENDPOINT + "/category/{category}", "Books")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", not(empty()))
                .body("data.category", everyItem(equalTo("Books")));
    }

    @Test
    @DisplayName("Get products by unknown category returns 200 with empty list")
    void getByCategory_whenUnknown_returnsEmptyList(TestInfo testInfo) {
        given()
                .filter(sessionFilter)
                .header(TEST_ID_HEADER, testId(testInfo))
                .when()
                .get(PRODUCTS_ENDPOINT + "/category/{category}", "NonExistentCategoryXYZ")
                .then()
                .statusCode(200)
                .body("data", empty());
    }

    // ---------- GET /api/products/search ----------

    @Test
    @DisplayName("Search products by keyword returns 200 and matching items")
    void searchProducts_returns200(TestInfo testInfo) {
        String createBody = validProductBody("UniqueSearchableGadget", "Gadgets", 3);

        given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(createBody)
                .when()
                .post(PRODUCTS_ENDPOINT)
                .then()
                .statusCode(201);

        given()
                .filter(sessionFilter)
                .header(TEST_ID_HEADER, testId(testInfo))
                .queryParam("keyword", "UniqueSearchableGadget")
                .when()
                .get(PRODUCTS_ENDPOINT + "/search")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", not(empty()));
    }

    @Test
    @DisplayName("Search products with missing keyword param returns 400")
    void searchProducts_withMissingKeyword_returns400(TestInfo testInfo) {
        given()
                .filter(sessionFilter)
                .header(TEST_ID_HEADER, testId(testInfo))
                .when()
                .get(PRODUCTS_ENDPOINT + "/search")
                .then()
                .statusCode(400);
    }

    // ---------- PUT /api/products/{id} ----------

    @Test
    @DisplayName("Update existing product returns 200 and updated data")
    void updateProduct_whenExists_returns200(TestInfo testInfo) {

        String createBody = validProductBody("Original Name 1", "Misc", 20);

        Response res = given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(createBody)
                .when()
                .post(PRODUCTS_ENDPOINT)
                .then()
                .statusCode(201)
                .extract()
                .response();

        String productId = res.path("data.id");
        String sku = res.path("data.sku");

        usedProductIds.add(productId);

        String updateBody = """
                {
                  "name": "Updated Name",
                  "description": "Updated description",
                  "price": 59.99,
                  "category": "Misc",
                  "stockQuantity": 30,
                  "sku": "%s-UPDATED"
                }
                """.formatted(sku);

        given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(updateBody)
                .when()
                .put(PRODUCTS_ENDPOINT + "/{id}", productId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Product updated successfully"))
                .body("data.name", equalTo("Updated Name"))
                .body("data.price", equalTo(59.99f));
    }

    @Test
    @DisplayName("Update non-existent product returns 404")
    void updateProduct_whenNotFound_returns404(TestInfo testInfo) {
        UUID randomId = UUID.randomUUID();
        String updateBody = validProductBody("Ghost Product", "Misc", 1);

        given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(updateBody)
                .when()
                .put(PRODUCTS_ENDPOINT + "/{id}", randomId)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Update product with invalid payload returns 400")
    void updateProduct_withInvalidPayload_returns400(TestInfo testInfo) {
        String createBody = validProductBody("To Be Updated Badly", "Misc", 20);

        String productId = given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(createBody)
                .when()
                .post(PRODUCTS_ENDPOINT)
                .then()
                .statusCode(201)
                .extract()
                .path("data.id");

        usedProductIds.add(productId);

        String invalidUpdateBody = """
                {
                  "description": "Missing name field",
                  "price": 59.99,
                  "category": "Misc",
                  "stockQuantity": 30,
                  "sku": "SKU-INVALID-UPDATE"
                }
                """;

        given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(invalidUpdateBody)
                .when()
                .put(PRODUCTS_ENDPOINT + "/{id}", productId)
                .then()
                .statusCode(400);
    }

    // ---------- DELETE /api/products/{id} ----------

    @Test
    @DisplayName("Delete existing product returns 200 and success message")
    void deleteProduct_whenExists_returns200(TestInfo testInfo) {
        String createBody = validProductBody("To Be Deleted", "Misc", 1);

        String productId = given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .header(TEST_ID_HEADER, testId(testInfo))
                .body(createBody)
                .when()
                .post(PRODUCTS_ENDPOINT)
                .then()
                .statusCode(201)
                .extract()
                .path("data.id");

        given()
                .filter(sessionFilter)
                .header(TEST_ID_HEADER, testId(testInfo))
                .when()
                .delete(PRODUCTS_ENDPOINT + "/{id}", productId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Product deleted successfully"));

        // Confirm it's actually gone
        given()
                .filter(sessionFilter)
                .header(TEST_ID_HEADER, testId(testInfo))
                .when()
                .get(PRODUCTS_ENDPOINT + "/{id}", productId)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Delete non-existent product returns 404")
    void deleteProduct_whenNotFound_returns404(TestInfo testInfo) {
        UUID randomId = UUID.randomUUID();

        given()
                .filter(sessionFilter)
                .header(TEST_ID_HEADER, testId(testInfo))
                .when()
                .delete(PRODUCTS_ENDPOINT + "/{id}", randomId)
                .then()
                .statusCode(404);
    }

    @AfterAll
    static void tearDown() {
        // Delete all products created

        for(String productId: usedProductIds) {
            given()
                    .filter(sessionFilter)
                    .when()
                    .delete(PRODUCTS_ENDPOINT + "/{id}", productId)
                    .then()
                    .statusCode(200)
                    .body("success", equalTo(true))
                    .body("message", equalTo("Product deleted successfully"));
        }
    }
}
