package rag_system.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rag_system.demo.Entity.User;
import rag_system.demo.Service.UserService;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("create")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("update/{userName}")
    @PreAuthorize("hasRole('client_user')")
    public User updatUser(@PathVariable("userName") String userName, @RequestBody User user) {
        return userService.updateUser(userName, user);
    }

    @GetMapping("search/{userInfo}")
    @PreAuthorize("hasRole('client_user')")
    public User searchUser(@PathVariable("userInfo") String userName) {
        return userService.searchUser(userName);
    }

    @DeleteMapping("delete/{name}")
    @PreAuthorize("hasRole('client_user')")
    public void deleteUser(@PathVariable("name") String userName) {
        userService.deleteuser(userName);
    }
}
