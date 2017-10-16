package net.troja.eve.pve.web;

import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;

import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertThat;

public class MainControllerTest {
    private final MainController classToTest = new MainController();

    @Test
    public void indexNormal() {
        final String result = classToTest.index(null);

        assertThat(result, equalTo("index"));
    }

    @Test
    public void indexAuthenticated() {
        final String result = classToTest.index(new TestingAuthenticationToken(null, null));

        assertThat(result, equalTo("redirect:/sites"));
    }
}
