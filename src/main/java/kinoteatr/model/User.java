package kinoteatr.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "login", nullable = false)
    @Size(min = 4, message = "Логін повинен бути більше 4 символів")
    @Size(max = 50, message = "Логін повинен бути коротше 30 символів")
    private String username;

    @Column(name = "password", nullable = false)
    @Size(min = 6, message = "Пароль повинен бути більше 6 символів")
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "email", nullable = false)
    @NotBlank(message = "Введіть електронну адресу")
    @Email(message = "Введіть справжню електронну адресу")
    private String email;

    @Column(name = "phone", nullable = false)
    @Pattern(regexp = "\\d{9}", message = "Введіть останні 9 цифр номеру")
    private String phone;

    @Column(name = "name", nullable = false)
    @Size(max = 50, message = "Ім'я не може перевищувати 50 букв")
    @NotBlank(message = "Введіть справжнє ім'я")
    private String name;

    @Column(name = "surname", nullable = false)
    @Size(max = 50, message = "Прізвище не може перевищувати 50 букв")
    @NotBlank(message = "Введіть справжнє прізвище")
    private String surname;

    @Column(name = "active", nullable = false)
    private boolean isEnabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}