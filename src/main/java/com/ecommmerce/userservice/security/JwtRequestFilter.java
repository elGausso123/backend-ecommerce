/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ecommmerce.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author abrah
 */

@Component
public class JwtRequestFilter extends OncePerRequestFilter{
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        
        //Extrae el encabezado "Authorization" de la solicitud HTTP.
        final String authorizationHeader = request.getHeader("Authorization");
        
        //Inicializa la variable username,qué almacenará el nombre de usuario.
        String username = null;
        //Inicializa la variable jwt, qué almacenará el token JWT.
        String jwt = null;
        
        //Verifica si el encabezado contiene un token JWT con el prefijo "Bearer".
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
            //Extrae el token JWT eliminando los 7 primeros caracteres ("Bearer").
            jwt = authorizationHeader.substring(7);
            //Utiliza jwtUtil para extraer el nombre de usuario del token.
            username = jwtUtil.extractUsername(jwt);
        }
        
        //Si el nombre de usuario no es nulo, y el contexto de seguridad está vacio.
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            //Carga los detalles del usuario basado en el nombre de usuario.
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            //Si es un token válido con los detalles del usuario
            if(jwtUtil.validateToken(jwt, userDetails)){
                //Crea un token de autenticación con los detalles del usuario
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                
                //Establece detalles adicionales de la autenticación con la request.
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                //Coloca el token de autenticación en el contexto de Seguridad de Spring con los roles y permisos apropiados.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //Continua con la cadena de filtros, permitiendo que otros filtros procesen la información.
    chain.doFilter(request,response);    
    }
    
}
