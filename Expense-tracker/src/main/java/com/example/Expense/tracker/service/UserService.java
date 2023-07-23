package com.example.Expense.tracker.service;


import com.example.Expense.tracker.model.AuthenticationToken;
import com.example.Expense.tracker.model.Product;
import com.example.Expense.tracker.model.User;
import com.example.Expense.tracker.model.dto.SignInInput;
import com.example.Expense.tracker.model.dto.SignUpOutput;
import com.example.Expense.tracker.repository.IAuthTokenRepo;
import com.example.Expense.tracker.repository.IUserRepo;
import com.example.Expense.tracker.service.utility.EmailHandler;
import com.example.Expense.tracker.service.utility.PasswordEncrypter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    IUserRepo userRepo;
    @Autowired
    ProductService productService;
    @Autowired
    IAuthTokenRepo authTokenRepo;

    public SignUpOutput signUpUser(User user) {

        boolean signUpStatus = true;
        String signUpStatusMessage = null;

        String newEmail = user.getUserEmail();

        if(newEmail == null)
        {
            signUpStatusMessage = "Invalid email";
            signUpStatus = false;
            return new SignUpOutput(signUpStatus,signUpStatusMessage);
        }

        //check if this user email already exists ??
        User existingUser=userRepo.findFirstByUserEmail(newEmail);

        if( existingUser != null)
        {
            signUpStatusMessage = "Email already registered!!!";
            signUpStatus = false;
            return new SignUpOutput(signUpStatus,signUpStatusMessage);
        }

        //hash the password: encrypt the password
        try {
            String encryptedPassword = PasswordEncrypter.encryptPassword(user.getUserPassword());

            //save user with the new encrypted password

            user.setUserPassword(encryptedPassword);
            userRepo.save(user);

            return new SignUpOutput(signUpStatus, "User registered successfully!!!");
        }
        catch(Exception e)
        {
            signUpStatusMessage = "Internal error occurred during sign up";
            signUpStatus = false;
            return new SignUpOutput(signUpStatus,signUpStatusMessage);
        }
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public String signInPatient(SignInInput signInInput) {


        String signInStatusMessage = null;

        String signInEmail = signInInput.getEmail();

        if(signInEmail == null)
        {
            signInStatusMessage = "Invalid email";
            return signInStatusMessage;


        }

        //check if this patient email already exists ??
        User existingUser = userRepo.findFirstByUserEmail(signInEmail);

        if(existingUser == null)
        {
            signInStatusMessage = "Email not registered!!!";
            return signInStatusMessage;

        }

        //match passwords :

        //hash the password: encrypt the password
        try {
            String encryptedPassword = PasswordEncrypter.encryptPassword(signInInput.getPassword());
            if(existingUser.getUserPassword().equals(encryptedPassword))
            {
                //session should be created since password matched and user id is valid
                AuthenticationToken authToken  = new AuthenticationToken(existingUser);
                authTokenRepo.save(authToken);

                EmailHandler.sendEmail(signInEmail,"email testing",authToken.getTokenValue());
                return "Token sent to your email";
            }
            else {
                signInStatusMessage = "Invalid credentials!!!";
                return signInStatusMessage;
            }
        }
        catch(Exception e)
        {
            signInStatusMessage = "Internal error occurred during sign in";
            return signInStatusMessage;
        }

    }

    public String signOutUser(String email) {

        User user = userRepo.findFirstByUserEmail(email);
        authTokenRepo.delete(authTokenRepo.findFirstByUser(user));
        return "User Signed out successfully";
    }

    public String addProduct(Product product){
        return productService.addProduct(product);
    }

    public String deleteProduct(Long productId){
        Optional<Product> allProducts=productService.productRepo.findById(productId);
        Product product=allProducts.get();
        if(allProducts!=null){
            return productService.deleteProduct(product);
        }
        else
             return null;
    }

    public List<Product> monthlyProductList(String userEmail, LocalDate date1, LocalDate date2){
        User user= userRepo.findFirstByUserEmail(userEmail);
        if(user==null)
            return null;

        List<Product> productList=null;

        for(Product product: productService.productRepo.findAll()){
            if(product.getDate().isAfter(date1) && product.getDate().isAfter(date2)){
                productList.add(product);
            }
        }

        return productList;

    }

    public Double getMonthlyExpenditure(String userEmail, LocalDate date1, LocalDate date2){
        Double expenditure=0.0;
        List<Product> productList=monthlyProductList(userEmail,date1,date2);
        if(productList!=null) {

            for (Product product : productList) {
                expenditure+=product.getPrice();
            }
        }
        return expenditure;
    }






}
