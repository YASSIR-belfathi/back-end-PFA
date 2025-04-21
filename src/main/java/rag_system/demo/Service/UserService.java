package rag_system.demo.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rag_system.demo.Entity.User;
import rag_system.demo.Repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        Optional<User> optional_user = userRepository.findByUserName(user.getUserName());

        if (optional_user.isPresent()) {
            throw new IllegalArgumentException("this user with UserName exists before");
        } else {
            return userRepository.save(user);
        }
    }

    public User updateUser(String user_name, User user) {
        Optional<User> optional_user = userRepository.findByUserName(user_name);

        if (optional_user.isPresent()) {
            User user_update = optional_user.get();

            user_update.setEmail(user.getEmail());
            user_update.setFirstName(user.getFirstName());
            user_update.setLastName(user.getLastName());
            user_update.setPhoto_label(user.getPhoto_label());
            user_update.setLanguage_user(user.getLanguage_user());

            return userRepository.save(user_update);
        } else {
            throw new IllegalArgumentException("this user doesn't exits");
        }
    }

    public void deleteuser(String user_name) {
        Optional<User> optional_user = userRepository.findByUserName(user_name);
        if (optional_user.isPresent()) {
            User user_delete = optional_user.get();

            userRepository.delete(user_delete);
        } else {
            throw new IllegalArgumentException("this user doesn't exists");
        }
    }

    public User searchUser(String user_name) {
        Optional<User> optional_user = userRepository.findByUserName(user_name);
        if (optional_user.isPresent()) {
            User user_search = optional_user.get();
            return user_search;
        } else {
            throw new IllegalArgumentException("this user don't exists");
        }
    }
}
