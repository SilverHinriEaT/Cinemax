package kinoteatr.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import kinoteatr.model.Token;
import kinoteatr.model.User;
import kinoteatr.repository.TokenRepository;
import kinoteatr.repository.UserRepository;
import kinoteatr.service.UserService;

import java.security.Principal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(final Principal principal) {
        if (principal != null && ((Authentication) principal).isAuthenticated()) {
            return "forward:/";
        } else {
            return "login";
        }
    }

    @Override
    public String register(final Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @Override
    public String registerSuccessfully(final User user, final BindingResult bindingResult) {
        if (userNameExists(user.getUsername())) {
            bindingResult.addError(new FieldError
                    ("user", "username", "Введений логін вже існує"));
        }
        if (userEmailExists(user.getEmail())) {
            bindingResult.addError(new FieldError
                    ("user", "email", "Введена пошта вже існує"));
        }
        if (bindingResult.hasErrors()) {
            return "register";
        } else {
            addUser(user);
            return "redirect:register?success";
        }
    }

    private void addUser(final User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        user.setRole("ROLE_USER");
        user.isEnabled();
        sendToken(user);
    }

    private void sendToken(final User user) {
        final String tokenValue = UUID.randomUUID().toString();
        final Token token = new Token();
        token.setValue(tokenValue);
        token.setUser(user);
        tokenRepository.save(token);
    }

    private boolean userEmailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }

    private boolean userNameExists(final String username) {
        return userRepository.findByUsername(username) != null;
    }
}