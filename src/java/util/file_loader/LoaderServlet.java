package cgwap.util.file_loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract Servlet defining methods for file provider serlvets.
 */
abstract class LoaderServlet extends HttpServlet {

    private static final long serialVersionUID        = 585237222858259444L;

    /**
     * The buffer size used by the file loader when printing the files to the user. Data in bytes.
     */
    private static final int  FILE_LOADER_BUFFER_SIZE = 10240;

    /**
     * The error page to forward to when loading file failed. 
     */
    protected static final String FILE_NOT_FOUND_ERROR_PAGE = "/error/404.html";

    /**
     * Whether the given Accept-header accepts the given content type.
     * 
     * @param acceptHeader The Accept-header.
     * @param toAccept The content type to accept.
     * @return True if the Accept-header accepts the given content type.
     */
    protected boolean accepts(String acceptHeader, String toAccept) {
        String[] acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
        Arrays.sort(acceptValues);
        return Arrays.binarySearch(acceptValues, toAccept) > -1
                || Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
                || Arrays.binarySearch(acceptValues, "*/*") > -1;
    }

    /**
     * Writes a file buffered to the response's OutputStream. If the user's browser accepts GZIP
     * encoding, it is used.
     * 
     * @param file the file to deliver
     * @param response the response to write into
     * @param acceptsGZip whether the user's browser accepts GZIP encoding
     * @throws IOException if an error occurred handling the file or writing to the output
     * @throws FileNotFoundException if the file to write could not be found
     */
    protected void writeFileToOutputstream(File file, HttpServletResponse response, boolean acceptsGZip)
            throws IOException, FileNotFoundException {
        // get content type from file name (if unknown, set default)
        String contentType = getServletContext().getMimeType(file.getName());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        contentType += "; charset=UTF-8";

        // prepare response
        response.reset();
        response.setBufferSize(LoaderServlet.FILE_LOADER_BUFFER_SIZE);
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "inline;filename=\"" + file.getName() + "\"");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Range", "bytes 0-" + (file.length() - 1) + "/" + file.length());
        if (acceptsGZip) {
            response.setHeader("Content-Encoding", "gzip");
        } else {
            response.setHeader("Content-Length", String.valueOf(file.length()));
        }

        // write file content to output stream
        RandomAccessFile input = null;
        OutputStream output = null;
        try {
            input = new RandomAccessFile(file, "r");
            output = response.getOutputStream();

            if (acceptsGZip) {
                output = new GZIPOutputStream(output, LoaderServlet.FILE_LOADER_BUFFER_SIZE);
            }

            byte[] buffer = new byte[LoaderServlet.FILE_LOADER_BUFFER_SIZE];
            int read;
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);
            }
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        }
    }

    /**
     * Whether the user's browser accepts GZIP encoding (indicated through the Accept-Encoding HTTP
     * header).
     * 
     * @param acceptEncodingHeader the Accept-Encoding HTTP header sent by the user's browser
     * @return whether the user's browser accepts GZIP
     */
    protected boolean useGZipEncoding(String acceptEncodingHeader) {
        return acceptEncodingHeader != null && accepts(acceptEncodingHeader, "gzip");
    }

}