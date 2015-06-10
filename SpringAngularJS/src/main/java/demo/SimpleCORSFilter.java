package demo;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class SimpleCORSFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		
		/*
		 * 
		 * Uncomment following code if you whant to call a GET or POST methods from AngularJS
		 * outside of the box - e.g. if you run AngularJS by grunt serve etc.
		 * 
		 * 
		 * Keep in mind that you have to also disable csrf in the UIApplication
		 */
		
		// uncomment if needed
		/*
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with, origin, content-type, accept");
		*/
		
		//response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		chain.doFilter(req, res);
	}

	public void init(FilterConfig filterConfig) {}

	public void destroy() {}

}
