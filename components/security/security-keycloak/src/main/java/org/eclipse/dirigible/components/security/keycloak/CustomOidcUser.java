package org.eclipse.dirigible.components.security.keycloak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

class CustomOidcUser extends DefaultOidcUser {

    private static final long serialVersionUID = 6970756030913509875L;

    private final Set<GrantedAuthority> additionalAuthorities;

    CustomOidcUser(OidcUser oidcUser, Set<GrantedAuthority> additionalAuthorities) {
        super(oidcUser.getAuthorities(), oidcUser.getIdToken(), oidcUser.getUserInfo());
        this.additionalAuthorities = additionalAuthorities;
    }

    CustomOidcUser(OidcUser oidcUser, String userNameAttributeName, Set<GrantedAuthority> additionalAuthorities) {
        super(oidcUser.getAuthorities(), oidcUser.getIdToken(), oidcUser.getUserInfo(), userNameAttributeName);
        this.additionalAuthorities = additionalAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<? extends GrantedAuthority> authorities = super.getAuthorities();

        List<GrantedAuthority> allAuthorities = new ArrayList<>(authorities);
        allAuthorities.addAll(additionalAuthorities);

        return Collections.unmodifiableCollection(allAuthorities);
    }

}
