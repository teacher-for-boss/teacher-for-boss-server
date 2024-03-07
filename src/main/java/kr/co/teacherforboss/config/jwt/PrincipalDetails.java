package kr.co.teacherforboss.config.jwt;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.util.GrandUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class PrincipalDetails implements UserDetails {

    private Long memberId;
    private String email;
    private String password;
    private Role role;

    public PrincipalDetails(Member member) {
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.password = member.getPwHash();
        this.role = member.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return GrandUtil.getAuthoritiesByUserType(this.role);
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO: return memberStatus == ACTIVE;
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO: return memberStatus == ACTIVE;
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
