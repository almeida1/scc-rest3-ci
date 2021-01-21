package com.fatec.scelv1.servico;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fatec.scelv1.controller.ClienteController;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;



public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    
	private JWTUtil jwtUtil;
	private AuthenticationManager authenticationManager;
	User user; //spring framework security
	Logger logger = LogManager.getLogger(JWTAuthenticationFilter.class);
    
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		logger.info(">>>>>> JWT Authentication filter chamado => ");
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
       logger.info(">>>>>> tentativa de autenticacao => ");
    	try {
            ApplicationUser creds = new ObjectMapper()
                    .readValue(req.getInputStream(), ApplicationUser.class);

            return this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
            
        } catch (IOException e) {
        	logger.info(">>>>>> erro na tentativa de autenticacao => ");
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
    	logger.info(">>>>>> sucesso na tentativa de autenticacao => ");
    	String username = ((User) auth.getPrincipal()).getUsername();
    	logger.info(">>>>>> user => " + username);
    	String token = jwtUtil.generateToken(username);
    	logger.info(">>>>>> token => " + token);
    	res.addHeader("Authorization","Bearer " + token);
    	//res.addHeader("access-control-expose-headers", "Authorization");
    	
        
                
 	}
    
}