package com.example.Expense.tracker.service;

import com.example.Expense.tracker.model.Product;
import com.example.Expense.tracker.repository.IProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    IProductRepo productRepo;

    public String addProduct(Product product){
        productRepo.save(product);
        return "Product has been added successfully.";
    }

    public String deleteProduct(Product product) {
        productRepo.delete(product);
        return "Product has been deleted successfully.";
    }
}
