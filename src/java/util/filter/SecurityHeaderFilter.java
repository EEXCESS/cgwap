package cgwap.util.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Adds some security headers to response. Explicitly these are for XSS, click-jacking and
 * SSL-Stripping protection. And finally caching of dynamic content is disabled, as nobody with
 * access to the user's computer should be able to read out the browser's cache. Especially not
 * proxies should be able to do so.
 * 
 */
public class SecurityHeaderFilter implements Filter {

    /**
     * Max-age value for HSTS (HTTP Strict Transport Security). Data in seconds.
     */
    private static final long HSTS_MAX_AGE_VALUE = 31536000L;

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {}

    /**
     * Processes the logic of the filter and adds security header to the response.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;

        // XSS
        res.setHeader("X-XSS-Protection", "1; mode=block");

        // CSP sh. http://www.heise.de/security/artikel/XSS-Bremse-Content-Security-Policy-1888522.html
        res.setHeader("Content-Security-Policy", "default-src 'none'; "// by default, allow no inclusion
                + "script-src 'self' 'unsafe-inline' 'unsafe-eval'; "  // js only from own domain (allow inline/eval)
                + "style-src 'self' 'unsafe-inline'; "                 // style only from own domain (allow inline/eval)
                + "img-src *; "                                        // images from everywhere (for html inclusions)
                + "object-src 'self'; "                                // plug-ins only from same domain 
                + "font-src 'self'; ");                                // fonts only from same domain
        res.setHeader("X-Content-Security-Policy", "default-src 'none'; "
                + "script-src 'self' 'unsafe-inline' 'unsafe-eval'; "
                + "style-src 'self' 'unsafe-inline'; "
                + "img-src *; "
                + "object-src 'self'; "
                + "font-src 'self' 'unsafe-inline'; "
                + "options inline-script inline-css; ");
        res.setHeader("X-Webkit-CSP", "default-src 'none'; "
                + "script-src 'self' 'unsafe-inline' 'unsafe-eval'; "
                + "style-src 'self' 'unsafe-inline'; "
                + "img-src *; "
                + "object-src 'self'; "
                + "font-src 'self'; ");

        // click-jacking
        res.setHeader("X-Frame-Options", "sameorigin");

        // SSL-Stripping Protection
        res.setHeader("Strict-Transport-Security", "max-age=" + SecurityHeaderFilter.HSTS_MAX_AGE_VALUE);

        // avoid caching of dynamic content
        if (request.getContentType() != null && request.getContentType().startsWith("text/html")) {
            res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP/1.1
            res.setHeader("Pragma", "no-cache"); // HTTP/1.0
            res.setDateHeader("Expires", 0); // Proxies
        }

        chain.doFilter(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {}

}
