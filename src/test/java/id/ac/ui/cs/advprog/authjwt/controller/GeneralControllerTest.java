package id.ac.ui.cs.advprog.authjwt.controller;

import id.ac.ui.cs.advprog.authjwt.testconfig.TestSecurityBeansConfig;
import id.ac.ui.cs.advprog.authjwt.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GeneralController.class)
@Import({SecurityConfig.class, TestSecurityBeansConfig.class})
@TestPropertySource(properties = {
        "jwt.secret=fakeTestSecretKeyThatIsLongEnoughForHmacSha",
        "jwt.expiration=3600000"
})
public class GeneralControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void helloWorldEndpointReturnsHelloWorld() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World"));
    }
}
