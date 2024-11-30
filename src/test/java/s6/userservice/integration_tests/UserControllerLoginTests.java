package s6.userservice.integration_tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import s6.userservice.dto.LoginRequest;
import s6.userservice.dto.LoginResponse;
import s6.userservice.servicelayer.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerLoginTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testLogin_Success() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest("jdoe@gmail.com", "jdoe123");
        LoginResponse response = new LoginResponse("Bearer 123");
        when(userService.login(request)).thenReturn(response);
        // Act
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login").contentType(APPLICATION_JSON_VALUE).content(
                        """
                                {
                                            "email": "jdoe@gmail.com",
                                            "password": "jdoe123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        // Assert
        verify(userService).login(request);
        String expectedJson = "{\"accessToken\":\"Bearer 123\"}";
        String actualJson = mvcResult.getResponse().getContentAsString();
        assertEquals(expectedJson, actualJson);
    }
}
