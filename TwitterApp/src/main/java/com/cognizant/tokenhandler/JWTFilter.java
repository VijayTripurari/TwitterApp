package com.cognizant.tokenhandler;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

@Component
public class JWTFilter extends GenericFilterBean {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest req = (HttpServletRequest) request;
		final String token = req.getHeader("token");
		if (token == null || !token.startsWith("Bearer "))
			throw new ServletException("401 - UNAUTHORIZED");
		try {
			final Claims claims = Jwts.parser().setSigningKey("a3VtYXJAZ21haWwuY29t").parseClaimsJws(token.substring(7))
					.getBody();
			req.setAttribute("claims", claims);
		} catch (final SignatureException e) {
			throw new ServletException("401 - UNAUTHORIZED");
		}
		chain.doFilter(request, response);
	}
}
