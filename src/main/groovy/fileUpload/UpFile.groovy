package fileUpload

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


public class UpFile extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        println("in  upload doppost ");
        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iter;

        String s = "failed to parse file ";
        try {
            iter = upload.getItemIterator(request);
            FileItemStream fileItem = iter.next();
            InputStream i = fileItem.openStream();
            s = IOUtils.toString(i, "UTF-8")
            println "s $s"

          //  s = t.parseToString(i);
        } catch (FileUploadException e1) {

            println "UpFile exception:  $e1"
            e1.printStackTrace();
            s = "#error processing file"
        }

        response.getWriter().println( s)
    }
}