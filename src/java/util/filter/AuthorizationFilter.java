package cgwap.util.filter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cgwap.data_access.UserDatabaseAccess;
import cgwap.entities.User;
import cgwap.util.exception_handler.ApplicationException;
import cgwap.util.file_loader.CSSLoaderServlet;
import cgwap.util.session.SessionBean;

/**
 * Filter managing the access to the facelets with the help of the userId stored
 * in the session.
 * 
 */
public class AuthorizationFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthorizationFilter.class.getName());

    private static final int ERROR_STATUS = HttpServletResponse.SC_NOT_FOUND;

    private static final String ERROR_FORWARD_TARGET = "/error/404.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
    }

    /**
     * Processes the logic of the filter and manages the access to the facelets
     * using the userId
     * from session.
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String accessedFile = getAccessedFile(request);
        String accessedFolder = getAccessedFolder(request);
        String accessedFileExtension = getAccessedFileExtension(accessedFile);

        // *************************************************
        // 1st auth check:
        // - grant access to resources files, REST API, error- and
        // general-facelets
        // - prevent access to .xhtml files
        // *************************************************
        boolean valid = (accessedFolder.equals("javax.faces.resource") || accessedFolder.equals("error")
                || accessedFolder.equals("public") || accessedFolder.equals("rest"));
        valid = valid || accessedFileExtension.equals(CSSLoaderServlet.ALLOWED_FILE_EXTENSION);
        valid = valid && !accessedFileExtension.equals(".xhtml");

        // only continue advanced authorization checks if necessary
        if (!valid) {
            // get current user
            Object sessionUserId = request.getSession().getAttribute(SessionBean.SESSION_USER_KEY);

            User user = null;
            if (sessionUserId != null && sessionUserId instanceof Integer) {
                user = new User((int) sessionUserId);
                try {
                    user = UserDatabaseAccess.getById(user);
                } catch (ApplicationException e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    request.getRequestDispatcher("/error/500.html").forward(request, response);
                }
            }

            // *************************************************
            // 2nd auth check: dependent on rights
            // *************************************************

            if (user != null) { // logged in
                valid = valid || (accessedFolder.equals("members"));

            }
        }

        // react accordingly: continue or forward to error-page
        logAccess(valid, accessedFolder, accessedFile, request);
        if (valid) {
            filterChain.doFilter(req, res);
        } else {
            response.setStatus(ERROR_STATUS);
            request.getRequestDispatcher(ERROR_FORWARD_TARGET).forward(request, response);
        }
    }

  

    private String getAccessedFile(HttpServletRequest request) {
        return new File(request.getRequestURI()).getName();
    }

    private String getAccessedFolder(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accessedFolder = "public";
        String contextPath = request.getContextPath() + "/";
        if (!uri.equals(contextPath)) {
            uri = uri.substring(contextPath.length());
            int slashIndex = uri.indexOf("/");
            if (slashIndex > -1) {
                accessedFolder = uri.substring(0, uri.indexOf("/"));
            }
        }

        return accessedFolder;
    }

    private String getAccessedFileExtension(String accessedFile) {
        String ext = "";
        if (accessedFile != null && !accessedFile.equals("")) {
            int pointIndex = accessedFile.lastIndexOf(".");
            if (pointIndex > -1) {
                ext = accessedFile.substring(pointIndex);
            }
        }
        return ext;
    }

    private void logAccess(boolean granted, String accessedFolder, String accessedFile, HttpServletRequest request) {
        StringBuilder logMessage = new StringBuilder("Access ");
        if (granted) {
            logMessage.append("granted");
        } else {
            logMessage.append("denied");
        }
        logMessage.append(" - URI: ");
        logMessage.append(request.getRequestURI());
        logMessage.append(" (zugegriffener Ordner: ");
        logMessage.append(accessedFolder);
        logMessage.append(", Datei: ");
        logMessage.append(accessedFile);
      
        logMessage.append(")");

        Level logLvl = (granted) ? Level.FINE : Level.INFO;

        LOGGER.log(logLvl, logMessage.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}
