package com.example.playmock;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlaymockApplicationTests {

    public static final String GITHUB_API = "https://api.github.com/users/torstenwerner";

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void shouldFetchFromGithub() {
        final ResponseEntity<GithubResponse> response = restTemplate.getForEntity(GITHUB_API, GithubResponse.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getName(), is("Torsten Werner"));
    }

    @Test
    public void shouldMockError() {
        final MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate.getRestTemplate()).build();
        mockServer.expect(requestTo(GITHUB_API))
                .andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND));
        final ResponseEntity<GithubResponse> response = restTemplate.getForEntity(GITHUB_API, GithubResponse.class);
        assertThat(response.getStatusCode(), is(NOT_FOUND));
        mockServer.verify();
    }

    @Test
    public void shouldMockSuccess() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final GithubResponse expectedResponse = new GithubResponse("Hildegunst von Mythenmetz");
        final MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate.getRestTemplate()).build();
        mockServer.expect(requestTo(GITHUB_API))
                .andExpect(method(GET))
                .andRespond(withStatus(OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsBytes(expectedResponse)));
        final ResponseEntity<GithubResponse> response = restTemplate.getForEntity(GITHUB_API, GithubResponse.class);
        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody(), is(expectedResponse));
        mockServer.verify();

    }

    public static class GithubResponse {
        private String name;

        public GithubResponse(@JsonProperty("name") String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GithubResponse that = (GithubResponse) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {

            return Objects.hash(name);
        }
    }
}
