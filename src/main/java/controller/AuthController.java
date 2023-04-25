package controller;

import entity.User;
import model.AuthModel;

/**
 * Responsible for handling log in
 */

public class AuthController {
    /**
     *
     * @param user requests a user object with username
     * @return a user object
     */
    public User auth(User user){
        AuthModel authModel = new AuthModel();
        return authModel.login(user);
    }
}
