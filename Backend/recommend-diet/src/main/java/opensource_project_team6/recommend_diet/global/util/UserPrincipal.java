package opensource_project_team6.recommend_diet.global.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserPrincipal implements UserDetails {
    private final Long id;
    private final String email;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id,
                         String email,
                         String username,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.authorities = authorities;
    }

    public Long getId() { return id;  }
    public String getEmail() { return email; }

    @Override public String getUsername()                     { return username; }
    @Override public String getPassword()                     { return null; } // getPassword()는 JWT 방식이면 null 반환해도 무방
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public boolean isAccountNonExpired()            { return true; }
    @Override public boolean isAccountNonLocked()             { return true; }
    @Override public boolean isCredentialsNonExpired()        { return true; }
    @Override public boolean isEnabled()                      { return true; }
}
