package com.mojagap.mojanode.infrastructure.security;

import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ApplicationConstants;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.repository.user.AppUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    @Autowired
    public AppUserRepository appUserRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authenticationToken = request.getHeader(ApplicationConstants.AUTHENTICATION_HEADER_NAME);

        if (authenticationToken == null) {
            chain.doFilter(request, response);
            return;
        }

        AppUser appUser = veryAuthenticationToken(authenticationToken);
        AppContext.setLoggedInUser(appUser);
        chain.doFilter(request, response);
    }

    private AppUser veryAuthenticationToken(String authentication) {
        String secretKey = Base64.getEncoder().encodeToString(ApplicationConstants.JWT_SECRET_KEY.getBytes());
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authentication).getBody();
        appUserRepository = AppContext.getBean(AppUserRepository.class);
        Integer userId = claims.get("userId", Integer.class);
        AppUser appUser = appUserRepository.getById(userId);
        List<GrantedAuthority> authorities = new ArrayList<>();
        appUser.getRole().getPermissions().stream().map(permission -> new SimpleGrantedAuthority(permission.getName())).forEach(authorities::add);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(appUser.getEmail(), null, authorities));
        return appUser;
    }
}
