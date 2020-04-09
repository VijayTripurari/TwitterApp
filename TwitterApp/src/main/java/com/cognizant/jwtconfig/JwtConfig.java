package com.cognizant.jwtconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cognizant.tokenhandler.JWTFilter;

@Configuration
public class JwtConfig {
	@Autowired
	JWTFilter jwtFilter;

	@Bean
	public FilterRegistrationBean<JWTFilter> filterRegistrationBean() {
		FilterRegistrationBean<JWTFilter> filterRegistrationBean = new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(jwtFilter);
		filterRegistrationBean.addUrlPatterns("/secure/*");
		return filterRegistrationBean;
	}
}
