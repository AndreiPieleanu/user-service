package s6.userservice.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import s6.userservice.configuration.security.isauthenticated.IsAuthenticated;
import s6.userservice.datalayer.entities.Role;
import s6.userservice.datalayer.entities.User;
import s6.userservice.dto.LoginRequest;
import s6.userservice.dto.LoginResponse;
import s6.userservice.dto.UserCreatedRequest;
import s6.userservice.requestresponse.*;
import s6.userservice.servicelayer.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreatedRequest userCreatedRequest) {
        User createdUser = userService.createUser(userCreatedRequest);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    @IsAuthenticated
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MODERATOR"})
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("{userId}")
    @IsAuthenticated
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"})
    public ResponseEntity<FindUserResponse> getUserById(@PathVariable(name =
            "userId")int userId){
        FindUserRequest request = new FindUserRequest(userId);
        return ResponseEntity.ok(userService.findUser(request));
    }

    @PutMapping("{userId}")
    @IsAuthenticated
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MODERATOR"})
    public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable(name =
            "userId")int userId, @RequestBody UpdateUserRequest request){
        request.setUserId(userId);
        return ResponseEntity.ok(userService.updateUser(request));
    }
    @PutMapping("roles/{userId}")
    @IsAuthenticated
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<UpdateRoleResponse> updateUserRole(@PathVariable(name =
            "userId")int userId, @RequestBody UpdateRoleRequest request){
        request.setId(userId);
        return ResponseEntity.ok(userService.updateUserRole(request));
    }

    @DeleteMapping("/{userId}")
    @IsAuthenticated
    @RolesAllowed({"ROLE_ADMIN", "ROLE_MODERATOR"})
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getRoles(){
        return ResponseEntity.ok(userService.getRoles());
    }
}
