package cgwap.util.file_loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cgwap.util.Config;

/**
 * Prints css stylesheets from not-server-accessible locations on system to user.
 * 
 */
@WebServlet("*.css")
public class CSSLoaderServlet extends LoaderServlet {

    private static final long   serialVersionUID       = 3906585381244822412L;

    private static final Logger LOGGER                 = Logger.getLogger(CSSLoaderServlet.class.getName());

    /**
     * Allowed file type (mime- / content-type) for css stylesheets to upload. When changing add or
     * remove appropriate extension to URL-Pattern above and change the extension bellow..
     */
    public static final String  ALLOWED_CONTENT_TYPE   = "text/css";

    /**
     * Extension allowed for uploaded css stylesheets. When changing add or remove appropriate
     * extension to URL-Pattern above and change the file type above
     */
    public static final String  ALLOWED_FILE_EXTENSION = ".css";

    /**
     * Defines the path from context root to css resources directory.
     */
    private static final String RESOURCES_PATH         = "/resources/css";

    /**
     * Initializes the Servlet and checks given base path.
     */
    @Override
    public void init() throws ServletException {
        String basePath = Config.CSS_STORING_LOCATION;

        if (basePath.equals("")) {
            LOGGER.warning("Config parameter 'CSS_STORING_LOCATION' is not defined. ");
        } else {
            File path = new File(basePath);
            if (!path.exists()) {
                LOGGER.warning("Config parameter 'CSS_STORING_LOCATION's value: \"" + basePath
                        + "\" does actually not exist in file system. ");
            } else if (!path.isDirectory()) {
                LOGGER.warning("Config parameter 'CSS_STORING_LOCATION's value: \"" + basePath
                        + "\" is actually not a directory in file system. ");
            } else if (!path.canRead()) {
                LOGGER.warning("Config parameter 'CSS_STORING_LOCATION's value: \"" + basePath
                        + "\" is actually not readable in file system. ");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        boolean acceptsGZip = useGZipEncoding(request.getHeader("Accept"));
        String requestedFileName = new File(URLDecoder.decode(request.getRequestURI(), "UTF-8")).getName();

        // check if file exists within the user-upload directory
        File uploadDirFile = new File(Config.CSS_STORING_LOCATION, requestedFileName);
        if (uploadDirFile.canRead()) {
            try {
                writeFileToOutputstream(uploadDirFile, response, acceptsGZip);
                return;
            } catch (FileNotFoundException e) {
                // log exception, but continue normally as if the File.exists() has returned false
                // instead of true
                LOGGER.log(Level.INFO, "FileNotFoundException although passed File.exists()", e);
            }
        }

        // otherwise get real path of requested file to check if it exists at the requested position
        String resourcePath = getServletContext()
                .getRealPath(CSSLoaderServlet.RESOURCES_PATH + "/" + requestedFileName);
        File resourceFile = new File(resourcePath);
        if (resourceFile.canRead()) {
            // if file does exist, hand it to the user
            try {
                writeFileToOutputstream(resourceFile, response, acceptsGZip);
                return;
            } catch (FileNotFoundException e) {
                // log exception, but continue normally as if the File.exists() has returned false
                // instead of true
                LOGGER.log(Level.INFO, "FileNotFoundException although passed File.exists()", e);
            }
        }

        // if file could not be found in one of the above locations, send an error
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.getRequestDispatcher(LoaderServlet.FILE_NOT_FOUND_ERROR_PAGE).forward(request, response);
    }

}
