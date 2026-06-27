package com.jide.framework.tests.post;

import com.jide.framework.assertions.ApiResponseAssert;
import com.jide.framework.client.RequestBuilder;
import com.jide.framework.models.Post;
import com.jide.framework.models.User;
import com.jide.framework.tests.BaseTest;
import com.jide.framework.validators.XmlSchemaValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * PostRequestsTest covers all POST scenarios.
 */
@Epic("REST API Framework")
@Feature("POST Requests")
public class PostRequestsTest extends BaseTest {

    // -------------------------------------------------------------------------
    // Successful POST — JSON
    // -------------------------------------------------------------------------

    @Test(description = "POST /users with POJO body returns 201 with echoed fields")
    @Story("Create user")
    @Severity(SeverityLevel.CRITICAL)
    public void createUser_withPojo_returns201() {
        User user = new User("Jide Framework", "jide.fw", "jide@fw.com");

        Response response = RequestBuilder.withJson()
            .body(user)
            .post("/users");

        ApiResponseAssert.assertThat(response)
            .hasStatus(201)
            .hasJsonPath("name", "Jide Framework")
            .hasJsonPath("username", "jide.fw")
            .hasJsonPath("email", "jide@fw.com")
            .hasJsonPathPresent("id");
    }

    @Test(description = "POST /users with raw JSON payload file returns 201")
    @Story("Create user")
    @Severity(SeverityLevel.CRITICAL)
    public void createUser_withPayloadFile_returns201() {
        String payload = loadPayload("payloads/json/create-user.json");

        Response response = RequestBuilder.withJson()
            .body(payload)
            .post("/users");

        ApiResponseAssert.assertThat(response)
            .hasStatus(201)
            .hasJsonPath("name", "Jide Framework User")
            .hasJsonPath("email", "jide@framework.com")
            .hasJsonPathPresent("id");
    }

    @Test(description = "POST /posts with POJO body returns 201 with correct userId")
    @Story("Create post")
    @Severity(SeverityLevel.CRITICAL)
    public void createPost_withPojo_returns201() {
        Post post = new Post(1, "Framework Post Title", "Body content for the post");

        Response response = RequestBuilder.withJson()
            .body(post)
            .post("/posts");

        ApiResponseAssert.assertThat(response)
            .hasStatus(201)
            .hasJsonPath("userId", 1)
            .hasJsonPath("title", "Framework Post Title")
            .hasJsonPath("body", "Body content for the post")
            .hasJsonPathPresent("id");
    }

    @Test(description = "POST /users with Map body returns 201")
    @Story("Create user")
    @Severity(SeverityLevel.NORMAL)
    public void createUser_withMap_returns201() {
        Map<String, Object> body = new HashMap<>();
        body.put("name",     "Map User");
        body.put("username", "mapuser");
        body.put("email",    "map@user.com");

        Response response = RequestBuilder.withJson()
            .body(body)
            .post("/users");

        ApiResponseAssert.assertThat(response)
            .hasStatus(201)
            .hasJsonPath("name", "Map User");
    }

    // -------------------------------------------------------------------------
    // POST — XML body
    // -------------------------------------------------------------------------

    @Test(description = "POST /users with XML payload is accepted")
    @Story("Create user — XML payload")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates XML payload against XSD and sends it using XML request headers.")
    public void createUser_withXmlPayload_returns201() {

        String xmlPayload = loadPayload("payloads/xml/create-user.xml");

        // JSONPlaceholder does not support real XML responses,
        // but we can still demonstrate sending XML and validating it.

        // Verify our XML is valid before sending
        XmlSchemaValidator.validate(
            xmlPayload,
            "schemas/xml/user-schema.xsd"
        );

        Response response = RequestBuilder.withXml()
            .body(xmlPayload)
            .post("/users");

        ApiResponseAssert.assertThat(response)
            .hasStatus(201);
    }

    // -------------------------------------------------------------------------
    // Edge cases
    // -------------------------------------------------------------------------

    @Test(description = "POST /users with empty body still returns a response")
    @Story("Edge cases — request body")
    @Severity(SeverityLevel.MINOR)
    public void createUser_withEmptyBody_returnsResponse() {
        Response response = RequestBuilder.withJson()
            .body("{}")
            .post("/users");

        // JSONPlaceholder returns 201 with an id even for empty body
        // A real API would return 400; this test demonstrates the behaviour
        // is observed and asserted explicitly.
        ApiResponseAssert.assertThat(response)
            .hasStatusIn(201, 400);
    }

    @Test(description = "POST /users with extra unknown fields returns 201 (API ignores extras)")
    @Story("Edge cases — request body")
    @Severity(SeverityLevel.MINOR)
    public void createUser_withExtraFields_returns201() {
        Map<String, Object> body = new HashMap<>();
        body.put("name",          "Extra Fields User");
        body.put("username",      "extra.fields");
        body.put("email",         "extra@fields.com");
        body.put("unknownField",  "this should be ignored by the API");

        Response response = RequestBuilder.withJson()
            .body(body)
            .post("/users");

        ApiResponseAssert.assertThat(response)
            .hasStatus(201)
            .hasJsonPath("name", "Extra Fields User");
    }
}