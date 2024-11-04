package s6.userservice.unit_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import s6.userservice.configuration.AccessToken;
import s6.userservice.datalayer.IUserDal;
import s6.userservice.datalayer.entities.Role;
import s6.userservice.datalayer.entities.User;
import s6.userservice.dto.*;
import s6.userservice.rabbitmq.RabbitMQProducer;
import s6.userservice.requestresponse.FindUserRequest;
import s6.userservice.requestresponse.FindUserResponse;
import s6.userservice.requestresponse.UpdateUserRequest;
import s6.userservice.requestresponse.UpdateUserResponse;
import s6.userservice.servicelayer.token.IAccessTokenEncoder;
import s6.userservice.servicelayer.UserService;
import s6.userservice.servicelayer.customexceptions.UserNotFoundException;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private IUserDal userDal;
    private PasswordEncoder passwordEncoder;
    private RabbitMQProducer rabbitMQProducer;
    private UserService userService;
    private IAccessTokenEncoder accessTokenEncoder;

    private final User addedUser =
            User.builder()
                    .id(1)
                    .role(Role.USER)
                    .email("jdoe@gmail.com")
                    .password("encodedPassword")
                    .firstName("John")
                    .lastName("Doe")
                    .build();

    @BeforeEach
    public void setUp(){
        userDal = mock(IUserDal.class);
        passwordEncoder = mock(PasswordEncoder.class);
        rabbitMQProducer = mock(RabbitMQProducer.class);
        accessTokenEncoder = mock(IAccessTokenEncoder.class);
        userService = new UserService(userDal, passwordEncoder, accessTokenEncoder, rabbitMQProducer);
    }

    @Test
    public void testCreateUser_Success() {
        UserCreatedRequest user = new UserCreatedRequest();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        Mockito.when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        Mockito.when(userDal.save(Mockito.any(User.class))).thenReturn(addedUser);
        Mockito.doNothing().when(rabbitMQProducer).publishUserCreatedEvent(any(UserCreatedEvent.class));

        User createdUser = userService.createUser(user);

        // Assertions
        assertNotNull(createdUser);
        assertEquals("John", createdUser.getFirstName());
        assertEquals("encodedPassword", createdUser.getPassword());
        Mockito.verify(userDal, times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void testCreateUser_PasswordEncoding() {
        UserCreatedRequest user = new UserCreatedRequest();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        Mockito.when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        Mockito.when(userDal.save(Mockito.any(User.class))).thenReturn(addedUser);
        Mockito.doNothing().when(rabbitMQProducer).publishUserCreatedEvent(any(UserCreatedEvent.class));
        User createdUser = userService.createUser(user);

        assertEquals("encodedPassword", createdUser.getPassword());
        Mockito.verify(passwordEncoder).encode("password");
    }

    @Test
    public void testGetUsers_Success() {
        List<User> users = Arrays.asList(
                new User(1, "John", "Doe", "john.doe@example.com", "encodedPassword", Date.from(Instant.now()), Date.from(Instant.now()), Role.USER, "", "", ""),
                new User(2, "Jane", "Doe", "jane.doe@example.com", "encodedPassword", Date.from(Instant.now()), Date.from(Instant.now()), Role.ADMIN, "", "", "")
        );

        Mockito.when(userDal.findAll()).thenReturn(users);

        List<User> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        Mockito.verify(userDal, times(1)).findAll();
    }

    @Test
    public void testDeleteUser_Success() {
        Integer userId = 1;

        // Mock the delete operation
        Mockito.doNothing().when(userDal).deleteById(userId);
        Mockito.doNothing().when(rabbitMQProducer).publishUserDeletedEvent(any(UserDeletedEvent.class));

        userService.deleteUser(userId);

        // Verify that deleteById was called once
        Mockito.verify(userDal, times(1)).deleteById(userId);
    }

    @Test
    public void testLogin_Success() {
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "password");
        User user = new User(1, "John", "Doe", "john.doe@example.com", "encodedPassword", Date.from(Instant.now()), Date.from(Instant.now()), Role.USER, "", "", "");

        Mockito.when(userDal.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        Mockito.when(accessTokenEncoder.encode(Mockito.any(AccessToken.class))).thenReturn("accessToken");

        LoginResponse loginResponse = userService.login(loginRequest);

        assertNotNull(loginResponse);
        assertEquals("accessToken", loginResponse.getAccessToken());
        Mockito.verify(userDal, times(1)).findByEmail(loginRequest.getEmail());
    }

    @Test
    public void testLogin_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest("unknown@example.com", "password");

        Mockito.when(userDal.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.login(loginRequest));
        Mockito.verify(userDal, times(1)).findByEmail(loginRequest.getEmail());
    }

    @Test
    public void testFindUser_Success() {
        FindUserRequest findUserRequest = new FindUserRequest(1);
        User user = new User(1, "John", "Doe", "john.doe@example.com", "encodedPassword", Date.from(Instant.now()), Date.from(Instant.now()), Role.USER, "", "", "");

        Mockito.when(userDal.findById(findUserRequest.getUserId())).thenReturn(Optional.of(user));

        FindUserResponse response = userService.findUser(findUserRequest);

        assertNotNull(response);
        assertEquals(user.getEmail(), response.getFoundUser().getEmail());
        Mockito.verify(userDal, times(1)).findById(findUserRequest.getUserId());
    }

    @Test
    public void testFindUser_UserNotFound() {
        FindUserRequest findUserRequest = new FindUserRequest(999);

        Mockito.when(userDal.findById(findUserRequest.getUserId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUser(findUserRequest));
        Mockito.verify(userDal, times(1)).findById(findUserRequest.getUserId());
    }

    @Test
    public void testUpdateUser_Success() {
        UpdateUserRequest request = new UpdateUserRequest(1, "newemail@example.com", "NewFirstName", "NewLastName", "newPassword", Role.ADMIN, "", "", "");
        User existingUser = new User(1, "OldFirstName", "OldLastName", "oldemail@example.com", "oldPassword", Date.from(Instant.now()), Date.from(Instant.now()), Role.USER, "", "", "");
        User updatedUser = new User(1, "NewFirstName", "NewLastName", "newemail@example.com", "encodedNewPassword", Date.from(Instant.now()), Date.from(Instant.now()), Role.ADMIN, "", "", "");

        Mockito.when(userDal.findById(request.getUserId())).thenReturn(Optional.of(existingUser));
        Mockito.when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedNewPassword");
        Mockito.doNothing().when(userDal).updateUser(Mockito.any(User.class));

        UpdateUserResponse response = userService.updateUser(request);

        assertNotNull(response);
        assertEquals(request.getUserId(), response.getUpdatedId());
        Mockito.verify(userDal, times(1)).updateUser(Mockito.any(User.class));
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        UpdateUserRequest request = new UpdateUserRequest(999, "newemail@example.com", "NewFirstName", "NewLastName", "newPassword", Role.ADMIN, "", "", "");

        Mockito.when(userDal.findById(request.getUserId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(request));
        Mockito.verify(userDal, times(1)).findById(request.getUserId());
    }
}
