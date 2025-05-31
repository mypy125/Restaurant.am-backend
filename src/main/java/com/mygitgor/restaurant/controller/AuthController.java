package com.mygitgor.restaurant.controller;

import com.mygitgor.restaurant.config.JwtProvider;
import com.mygitgor.restaurant.domain.Cart;
import com.mygitgor.restaurant.domain.Role;
import com.mygitgor.restaurant.domain.User;
import com.mygitgor.restaurant.repository.CartRepository;
import com.mygitgor.restaurant.repository.UserRepository;
import com.mygitgor.restaurant.controller.DTOs.request.LoginRequest;
import com.mygitgor.restaurant.controller.DTOs.request.RegisterRequest;
import com.mygitgor.restaurant.controller.DTOs.response.AuthResponse;
import com.mygitgor.restaurant.service.CustomerUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * AuthController определяется конечные точки, связанные с аутентификацией и авторизацией пользователей.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomerUserDetailsService customerUserDetailsService;


    /**
     * Этот метод является обработчиком создания новой учетной записи пользователя.
     * Получает объект User из тела запроса.
     * Создает новый объект User на основе полученных данных.
     * Хэширует пароль пользователя с использованием passwordEncoder.
     * @param request объект из тела запроса
     * @return возврошает JWT токен, сообщение об успешной регистрации и роль пользователя.
     * @throws Exception бросает исключения Exception
     */
    @CrossOrigin(origins = {
            "http://localhost:3000",
            "https://restaurant-frontend-9jwn.onrender.com"
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody RegisterRequest request) throws Exception {
        User isEmailExist = userRepository.findByEmail(request.getEmail());
        if(isEmailExist != null){
            throw new Exception("Email is already used with another account..");
        }
        User createUser = new User();
        createUser.setEmail(request.getEmail());
        createUser.setFullName(request.getFullName());
        createUser.setRole(Role.valueOf(request.getRole()));
        createUser.setPassword(passwordEncoder.encode(request.getPassword()));
        User saveUser = userRepository.save(createUser);

        Cart cart = new Cart();
        cart.setCustomer(saveUser);
        cartRepository.save(cart);

        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMassage("Register success");
        authResponse.setRole(saveUser.getRole());

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }



    /**
     * Этот метод отвечает за аутентификацию пользователя и генерацию JWT токена при успешной аутентификации.
     * @param request данные аутентификации (логин и пароль)
     * @return возврошает JWT токен, сообщение об успешной аутентификации и роль пользователя.
     */
    @CrossOrigin(origins = {
            "http://localhost:3000",
            "https://restaurant-frontend-9jwn.onrender.com"
    })
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@RequestBody LoginRequest request){
        String username = request.getEmail();
        String password = request.getPassword();

        Authentication authentication = authenticate(username, password);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.isEmpty()?null:authorities.iterator().next().getAuthority();

        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMassage("Register success");
        authResponse.setRole(Role.valueOf(role));

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);
        if(userDetails == null){
            throw new BadCredentialsException("invalid username.");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("invalid password.");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}
