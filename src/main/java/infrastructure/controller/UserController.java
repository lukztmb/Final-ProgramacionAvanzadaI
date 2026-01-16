package infrastructure.controller;

import application.dto.request.ActivateUserRequestDTO;
import application.dto.request.UserRequestDTO;
import application.dto.response.UserResponseDTO;
import application.usecase.ActivateUser;
import application.usecase.RegisterUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {

    private final RegisterUser registerUser;
    private final ActivateUser activateUser;

    public UserController(RegisterUser registerUser, ActivateUser activateUser) {
        this.registerUser = registerUser;
        this.activateUser = activateUser;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO response = registerUser.registerUser(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activate(@Valid @RequestBody ActivateUserRequestDTO request) {
        activateUser.activate(request.email(), request.code());
        return ResponseEntity.noContent().build();
    }
}