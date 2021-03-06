package demo;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@SpringBootApplication
@RestController
public class UiApplication {


    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    

	public static void main(String[] args) {
		SpringApplication.run(UiApplication.class, args);
	}

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
	
	@Configuration
	@Order(1)
	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		
		@Autowired
	    private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

		@Autowired
	    private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

		@Autowired
	    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;		
	    
	    
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
	            .formLogin()
	            .loginProcessingUrl("/api/authentication")
	            .successHandler(ajaxAuthenticationSuccessHandler)
	            .failureHandler(ajaxAuthenticationFailureHandler)
	            .usernameParameter("j_username")
	            .passwordParameter("j_password")
	            .permitAll()
	         .and()
	            .logout()
	            .logoutUrl("/api/logout")
	            .logoutSuccessHandler(ajaxLogoutSuccessHandler)
	            .deleteCookies("JSESSIONID")
	            .permitAll()	            
			.and().authorizeRequests()
					.antMatchers("/index.html", "/home.html", "/login.html", "/*").permitAll().anyRequest()
					.authenticated()
			.and().csrf().disable();
			/*
			.csrfTokenRepository(csrfTokenRepository()).and()
			.addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
			*/
			
			// logout doesn't work properly with csrf filter above
			// disable csrf if you want logout and login action
			// from AngularJS perspective which works properly
			// .and().csrf().disable();
		}


	}
	

	/*
	@Configuration
	@Order(2)
	public static class BasicAuthSecurityConfigurationAdapter extends
			WebSecurityConfigurerAdapter {

		protected void configure(HttpSecurity http) throws Exception {

			// check hello.js greeting GET method
			http.authorizeRequests().antMatchers("/greeting").authenticated()
					.and().httpBasic().and().csrf()
					.csrfTokenRepository(csrfTokenRepository()).and()
					.addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
		}
		

	}
	*/

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.inMemoryAuthentication().withUser("daniels").password("123")
				.roles("USER", "TESTER");
	}
	
	private static  Filter csrfHeaderFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request,
					HttpServletResponse response, FilterChain filterChain)
					throws ServletException, IOException {
				CsrfToken csrf = (CsrfToken) request
						.getAttribute(CsrfToken.class.getName());
				if (csrf != null) {
					Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
					String token = csrf.getToken();
					if (cookie == null || token != null
							&& !token.equals(cookie.getValue())) {
						cookie = new Cookie("XSRF-TOKEN", token);
						cookie.setPath("/");
						response.addCookie(cookie);
					}
				}
				filterChain.doFilter(request, response);
			}
		};
	}

	private static CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}

}
