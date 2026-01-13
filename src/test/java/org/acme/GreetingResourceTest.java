package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class InvoiceResourceTest {

    @Test
    void create_invoice_success() {

        given()
                .contentType("application/json")
                .body("""
                {
                  "customerName": "ACME Corp",
                  "customerEmail": "billing@acme.com",
                  "currency": "EUR",
                  "issueDate": "2026-01-13",
                  "dueDate": "2026-02-13",
                  "lines": [
                    {
                      "description": "Consulting",
                      "quantity": 2,
                      "unitPriceCents": 50000
                    },
                    {
                      "description": "Support",
                      "quantity": 1,
                      "unitPriceCents": 15000
                    }
                  ]
                }
            """)
                .when()
                .post("/api/invoices")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("status", is("DRAFT"))
                .body("totalCents", is(115000))
                .body("lines", hasSize(2))
                .body("lines[0].lineTotalCents", is(100000))
                .body("lines[1].lineTotalCents", is(15000));
    }

    @Test
    void get_invoice_by_id() {

        Long id =
                given()
                        .contentType("application/json")
                        .body("""
                    {
                      "customerName": "Client X",
                      "currency": "EUR",
                      "issueDate": "2026-01-10",
                      "dueDate": "2026-02-10",
                      "lines": [
                        {
                          "description": "Development",
                          "quantity": 1,
                          "unitPriceCents": 80000
                        }
                      ]
                    }
                """)
                        .when()
                        .post("/api/invoices")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("id");

        given()
                .when()
                .get("/api/invoices/{id}", id)
                .then()
                .statusCode(200)
                .body("id", is(id.intValue()))
                .body("totalCents", is(80000))
                .body("lines", hasSize(1));
    }

    @Test
    void create_invoice_validation_error() {

        given()
                .contentType("application/json")
                .body("""
                {
                  "currency": "EUR",
                  "issueDate": "2026-01-13",
                  "dueDate": "2026-02-13",
                  "lines": []
                }
            """)
                .when()
                .post("/api/invoices")
                .then()
                .statusCode(400);
    }

    @Test
    void get_invoice_not_found() {

        given()
                .when()
                .get("/api/invoices/{id}", 999999)
                .then()
                .statusCode(404);
    }
}
