package com.old.silence.webmvc.test.functional;

import jakarta.annotation.Resource;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.old.silence.core.test.functional.IntegrationTests;

/**
 * @author moryzang
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestControllerIntegrationTests extends IntegrationTests {

    protected TestRestTemplate testRestTemplate;

    protected RestTemplate restTemplate;

    @Resource(name = "org.springframework.boot.test.web.client.TestRestTemplate")
    public void setTestRestTemplate(TestRestTemplate testRestTemplate) {
        this.testRestTemplate = Objects.requireNonNull(testRestTemplate);
        this.restTemplate = testRestTemplate.getRestTemplate();
    }

    protected <T> void verifyPage(ResponseEntity<List<T>> responseEntity, int expectedTotalCount,
                                  int expectedResultCount) {
        verifyHttpHeader(responseEntity, "X-Total-Count", expectedTotalCount);
        assertThat(responseEntity.getBody()).hasSizeGreaterThanOrEqualTo(expectedResultCount);
    }

    protected static <T> void verifyHttpHeader(ResponseEntity<List<T>> responseEntity, String headerName,
                                               Object expectedValue) {
        assertThat(responseEntity.getHeaders().getFirst(headerName)).isEqualTo(expectedValue.toString());
    }
}
