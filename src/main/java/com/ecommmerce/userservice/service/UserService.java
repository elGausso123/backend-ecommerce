/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecommmerce.userservice.service;

import com.ecommmerce.userservice.entity.User;
import com.ecommmerce.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author abrah
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(User user){
        //Verifica si el mail existe
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("El email ya está registrado");
        }
        //Encriptamos la contraseña
        user.setPassword( passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
}
