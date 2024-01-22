package kr.co.teacherforboss.util;

import kr.co.teacherforboss.domain.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public class GrandUtil {

    public static List<GrantedAuthority> getAuthoritiesByUserType(Role role) {
        String roleName = role.name();
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName));
    }
}