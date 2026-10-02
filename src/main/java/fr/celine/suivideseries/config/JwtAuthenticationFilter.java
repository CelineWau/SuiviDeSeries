package fr.celine.suivideseries.config;

import fr.celine.suivideseries.service.JwtService;
import java.io.IOException;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String enTeteAuthorization =  request.getHeader("Authorization");

        if (enTeteAuthorization == null || !enTeteAuthorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = enTeteAuthorization.substring(7);

        try {
            String username = jwtService.extraireUsername(token);

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails utilisateur = userDetailsService.loadUserByUsername(username);

                if(jwtService.tokenValide(token, utilisateur)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(utilisateur, null, utilisateur.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
        }


        filterChain.doFilter(request, response);
    }
}