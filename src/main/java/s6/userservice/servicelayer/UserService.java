package s6.userservice.servicelayer;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import s6.userservice.configuration.AccessToken;
import s6.userservice.datalayer.IUserDal;
import s6.userservice.datalayer.entities.Role;
import s6.userservice.datalayer.entities.User;
import s6.userservice.dto.*;
import s6.userservice.rabbitmq.RabbitMQProducer;
import s6.userservice.requestresponse.*;
import s6.userservice.servicelayer.customexceptions.UserNotFoundException;
import s6.userservice.servicelayer.token.IAccessTokenEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final IUserDal userDal;
    private final PasswordEncoder passwordEncoder;
    private final IAccessTokenEncoder accessTokenEncoder;
    private final RabbitMQProducer rabbitMQProducer;

    public User createUser(UserCreatedRequest userCreatedRequest) {
        User user = new User();
        user.setFirstName(userCreatedRequest.getFirstName());
        user.setLastName(userCreatedRequest.getLastName());
        user.setEmail(userCreatedRequest.getEmail());
        user.setPassword(userCreatedRequest.getPassword());
        user.setRole(userCreatedRequest.getRole());
        user.setBio(userCreatedRequest.getBio());
        user.setLocation(userCreatedRequest.getLocation());
        user.setWebsite(userCreatedRequest.getWebsite());

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User createdUser = userDal.save(user);
        UserCreatedEvent userCreatedEvent = UserCreatedEvent
                .builder()
                .id(createdUser.getId())
                .email(createdUser.getEmail())
                .firstName(createdUser.getFirstName())
                .lastName(createdUser.getLastName())
                .build();
        rabbitMQProducer.publishUserCreatedEvent(userCreatedEvent);
        System.out.println("Message published! User: " + createdUser);
        return createdUser;
    }
    public List<User> getUsers(){
        return userDal.findAll();
    }
    public void deleteUser(Integer UserId) {
        userDal.deleteById(UserId);
        UserDeletedEvent event = new UserDeletedEvent(UserId);
        rabbitMQProducer.publishUserDeletedEvent(event);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> user = userDal.findByEmail(loginRequest.getEmail());
        List<User> abc = userDal.findAll();
        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }
        if(!matchesPassword(loginRequest.getPassword(), user.get().getPassword())) {
            throw new UserNotFoundException();
        }

        String accessToken = generateAccessToken(user.get());
        return LoginResponse.builder().accessToken(accessToken).build();
    }
    private boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    private String generateAccessToken(User entity){
        Integer userId = null;
        if(entity.getId() != null){
            userId = entity.getId();
        }

        String userRole = entity.getRole().toString();

        return accessTokenEncoder.encode(
                AccessToken
                        .builder()
                        .subject(entity.getEmail())
                        .role(userRole)
                        .userId(userId)
                        .build()
        );
    }

    public FindUserResponse findUser(FindUserRequest request) {
        Optional<User> foundEntity = userDal.findById(request.getUserId());
        if(foundEntity.isEmpty()) {
            throw new UserNotFoundException();
        }
        return new FindUserResponse(foundEntity.get());
    }

    @Transactional
    public UpdateUserResponse updateUser(UpdateUserRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User userToUpdate = User.builder()
                .id(request.getUserId())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(encodedPassword)
                .role(request.getRole())
                .bio(request.getBio())
                .location(request.getLocation())
                .website(request.getWebsite())
                .build();

        if(userDal.findById(request.getUserId()).isEmpty()){
            throw new UserNotFoundException();
        }
        userDal.updateUser(userToUpdate);
        return new UpdateUserResponse(request.getUserId());
    }
    public UpdateRoleResponse updateUserRole(UpdateRoleRequest request){
        User user = User.builder().id(request.getId()).role(request.getRole()).build();
        userDal.updateUserRole(user);
        return UpdateRoleResponse.builder().updatedId(request.getId()).build();
    }
    public List<Role> getRoles(){
        return Arrays.asList(Role.USER, Role.ADMIN, Role.MODERATOR);
    }
}
