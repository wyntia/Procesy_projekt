package pl.pollub.backend.config;
import pl.pollub.backend.exception.JwtAuthenticationException;
import pl.pollub.backend.service.auth.JwtUserDetailsService;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    public JwtRequestFilter(JwtUserDetailsService jwtUserDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (request.getHeader(AUTHORIZATION_HEADER) == null) {
            chain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader(AUTHORIZATION_HEADER);
        String jwtToken = null;
        String username = null;

        if (isTokenPresent(requestTokenHeader)) {
            jwtToken = extractToken(requestTokenHeader);

            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                throw new JwtAuthenticationException("Unable to get JWT Token", e);
            } catch (ExpiredJwtException e) {
                throw new JwtAuthenticationException("JWT Token has expired", e);
            }
        } else {
            throw new JwtAuthenticationException("JWT Token does not begin with Bearer String");
        }

        // Validate the token and set the security context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                setAuthentication(userDetails, request);
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isTokenPresent(String tokenHeader) {
        return tokenHeader != null && tokenHeader.startsWith(BEARER_PREFIX);
    }

    private String extractToken(String tokenHeader) {
        return tokenHeader.substring(BEARER_PREFIX.length());
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}