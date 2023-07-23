package com.example.Expense.tracker.controller;

import com.example.Expense.tracker.model.Product;
import com.example.Expense.tracker.model.User;
import com.example.Expense.tracker.model.dto.SignInInput;
import com.example.Expense.tracker.model.dto.SignUpOutput;
import com.example.Expense.tracker.service.AuthTokenService;
import com.example.Expense.tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    AuthTokenService authTokenService;

    @PostMapping("user/signup")
    public SignUpOutput signUpPatient(@RequestBody User user)
    {

        return userService.signUpUser(user);
    }

    @PostMapping("user/signIn")
    public String sigInUser(@RequestBody @Valid SignInInput signInInput)
    {
        return userService.signInPatient(signInInput);
    }

    @DeleteMapping("user/signOut")
    public String sigOutUser(String email, String token)
    {
        if(authTokenService.authenticate(email,token)) {
            return userService.signOutUser(email);
        }
        else {
            return "Sign out not allowed for non authenticated user.";
        }

    }
    @PostMapping("product")
    public String addProduct(@RequestBody Product product){
        return userService.addProduct(product);
    }

    @DeleteMapping("product/{productId}")
    public String deleteProduct(@PathVariable Long productId){
        return userService.deleteProduct(productId);
    }

    @GetMapping("monthlyProductList/{email}/{date1}/{date2}")
    public List<Product> getMonthlyProductList(@PathVariable String email,@PathVariable LocalDate date1,@PathVariable LocalDate date2){
        return userService.monthlyProductList(email,date1,date2);
    }

    @GetMapping("monthlyProductExpenditure/{email}/{date1}/{date2}")
    public Double getMonthlyProductExpenditure(@PathVariable String email,@PathVariable LocalDate date1,@PathVariable LocalDate date2){
        return userService.getMonthlyExpenditure(email,date1,date2);
    }





}
