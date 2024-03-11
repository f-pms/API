package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import io.restassured.builder.RequestSpecBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.web.server.LocalServerPort
import spock.lang.Shared


import static io.restassured.RestAssured.authentication
import static io.restassured.RestAssured.given
import static io.restassured.RestAssured.when
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.isEmptyOrNullString
import static org.hamcrest.Matchers.isEmptyString
import static org.hamcrest.Matchers.not

//@AutoConfigureMockMvc
class AlarmControllerSpec extends FunctionalTestSpec {
  private int port = 49198

  @Shared
  def requestSpec =
          new RequestSpecBuilder()
                  .setBaseUri("http://localhost:" + port)
                  .build()

  def "Should get blueprint with id = 1"() {
    given: "Set up request"
    def request = given(requestSpec)

    when: "Get blueprints"
    def response = request.get("/blueprints")

    then: "Should 200 and correct bluperint"
    response.then()
      .statusCode(200)
      .body("data",  not(isEmptyOrNullString()))
  }

//  @Autowired
//  private MockMvc mvc
//
//  @Autowired(required = false)
//  private AlarmConditionController webController
//
//  def "when context is loaded then all expected beans are created"() {
//    expect: "the WebController is created"
//    webController
//  }
//
//  def "when get is performed then the response has status 200 and content is 'Hello world!'"() {
//    expect: "Status is 200 and the response is 'Hello world!'"
//    mvc.perform(MockMvcRequestBuilders.get("/blueprints"))
//            .andExpect(status().isOk())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name",
//                    StringContains.containsString("name: must not be null"),
//            ) as ResultMatcher)
//  }
}
