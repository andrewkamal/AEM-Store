package com.aemstore.core.models;

import com.aemstore.core.servlets.CustomLogInServlet;
import org.apache.commons.codec.digest.Crypt;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = CustomLoginService.class, immediate = true)
public class CustomLoginService {

    private static final Logger log = LoggerFactory.getLogger(CustomLogInServlet.class);

    public Boolean checkUser(String email , String password){
        if ( email.equals("user@test.com")){
            log.info("\n ============== LOGIN ==========");
          //  log.info("\n Email: -> " + email + ", password -> " + password + ", encrypted password" + hashPassword(password));
            log.info("\n ============== LOGIN END ==========");
            return validatePassword(password , "$6$8DIfuSpu$CcKqiFjEdXZ1VwuExDbSfX4m4XeWswyDy9UvbJ0s1jVvlx5GIwNzeMhUoLlWgGaSMM/ZgrU3i7rCnMQ2Q.2wt0");
        }
        return false;
    }

    private String hashPassword(String plaintextPassword) {
        // Generate a salt
        String salt = Crypt.crypt(plaintextPassword);
        // Hash the password using bcrypt with the generated salt
        return Crypt.crypt(plaintextPassword, salt);
    }


    public boolean validatePassword(String userInputPassword, String storedHashedPassword) {
        // Extract the salt from the stored hashed password
        String salt = storedHashedPassword.substring(0, 29); // Bcrypt stores the salt as the first 29 characters

        // Hash the user input password using the extracted salt
        String hashedUserInputPassword = Crypt.crypt(userInputPassword, salt);

        // Compare the hashed user input password with the stored hashed password
        return hashedUserInputPassword.equals(storedHashedPassword);
    }

}
