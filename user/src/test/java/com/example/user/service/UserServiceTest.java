package com.example.user.service;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    /* ---------------- FETCH ALL USERS ---------------- */

    @DisplayName("Fetch All Users - Success")
    @Test
    void test_When_Fetch_All_Users_Success() {

        // Mock data
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");


        // Mock repository behavior
        when(userRepository.findAll())
                .thenReturn(List.of(user, user));


        // Call actual service method
        List<User> result = userService.fetchAllUsers();

        // Verify & assert
        verify(userRepository, times(1)).findAll();
        assertEquals(2, result.size());
    }

    /* ---------------- ADD USER ---------------- */

    @DisplayName("Add User - Success")
    @Test
    void test_When_Add_User_Success() {

        User user = getMockUser();

        // Mock save
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Call service
        userService.addUser(user);

        // Verify repository call
        verify(userRepository, times(1)).save(any(User.class));
    }

    /* ---------------- FETCH USER BY ID ---------------- */

    @DisplayName("Fetch User By Id - Success")
    @Test
    void test_When_Fetch_User_By_Id_Success() {

        User user = getMockUser();
        user.setId(1L);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.fetchUser(1L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        verify(userRepository).findById(anyLong());
    }

    @DisplayName("Fetch User By Id - Not Found")
    @Test
    void test_When_Fetch_User_Not_Found() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.fetchUser(1L);

        assertTrue(result.isEmpty());
        verify(userRepository).findById(anyLong());
    }

    /* ---------------- UPDATE USER ---------------- */

    @DisplayName("Update User - Success")
    @Test
    void test_When_Update_User_Success() {

        User existingUser = getMockUser();
        existingUser.setId(1L);

        User updatedUser = new User();
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.save(any(User.class)))
                .thenReturn(existingUser);

        boolean result = userService.updateUser(1L, updatedUser);

        assertTrue(result);
        assertEquals("Updated", existingUser.getFirstName());
        verify(userRepository).save(any(User.class));
    }

    @DisplayName("Update User - User Not Found")
    @Test
    void test_When_Update_User_Not_Found() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        boolean result = userService.updateUser(1L, getMockUser());

        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    /* ---------------- MOCK HELPER ---------------- */

    private User getMockUser() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        return user;
    }
}
