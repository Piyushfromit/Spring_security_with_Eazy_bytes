package com.eazybytes.controller;


import com.eazybytes.model.Customer;
import com.eazybytes.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody Customer customer){

        try {
           String hashPwd = passwordEncoder.encode(customer.getPwd());
           customer.setPwd(hashPwd);
           Customer customer2 =customerRepository.save(customer);
           if(customer2.getId() >0){
               return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
           }else{
               return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Registration failed");
           }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An Exception occurred" + e.getMessage());
        }
    }


}
