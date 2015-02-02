package cgwap.enums;

/**
 * All possible types of URL rewriting. The following types exist:
 * 
 * <ul>
 * <li>FORWARD: the user doesn't notice the redirect</li>
 * <li>REDIRECT: the browser gets a 301 status and a redirect-location - means the browser's URL is
 * changing and thus the redirect is visible to the user.</li>
 * </ul>
 * 
 * @author Benedikt Strobl
 */
public enum URLRewriteType {
    
    /**
     * the user doesn't notice the redirect
     */
    FORWARD, 
    
    /**
     * the browser gets a 301 status and a redirect-location - means the browser's URL is changing
     * and thus the redirect is visible to the user.
     */
    REDIRECT;
}
