package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUserHappyPath() {
        when(bCryptPasswordEncoder.encode("password")).thenReturn("hashedPassword");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Ahmed");
        request.setPassword("password");
        request.setConfirmPassword("password");
        ResponseEntity<User> response = userController.createUser(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        Assertions.assertNotNull(user);
        Assertions.assertEquals(0, user.getId());
        Assertions.assertEquals("Ahmed", user.getUsername());
        Assertions.assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    public void createUserUnhappyPath() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("password");
        request.setConfirmPassword("different_password");
        ResponseEntity<User> response = userController.createUser(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testGetInvalidUsername() {
        when(userRepository.findByUsername("user2")).thenReturn(null);
        ResponseEntity<User> user = userController.findByUserName("user2");
        Assertions.assertEquals(404, user.getStatusCodeValue());
    }

    @Test
    public void testGetValidUsername() {
        when(userRepository.findByUsername("test")).thenReturn(CartControllerTest.getTestUser());
        ResponseEntity<User> user = userController.findByUserName("test");
        Assertions.assertEquals(200, user.getStatusCodeValue());
    }

    @Test
    public void testGetInvalidUserId() {
        when(userRepository.findById(2l)).thenReturn(Optional.ofNullable(null));
        ResponseEntity<User> user = userController.findById(2l);
        Assertions.assertEquals(404, user.getStatusCodeValue());
    }

    @Test
    public void testGetValidUserId() {
        when(userRepository.findById(1l)).thenReturn(Optional.of(CartControllerTest.getTestUser()));
        ResponseEntity<User> user = userController.findById(1l);
        Assertions.assertEquals(200, user.getStatusCodeValue());
    }
    @Test
    public void createUserPasswordTooShort() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("Ahmed");
        r.setPassword("12345");
        r.setConfirmPassword("12345");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUserPasswordMismatch() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("Ahmed");
        r.setPassword("abcdefg");
        r.setConfirmPassword("abCdefg");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }
}
