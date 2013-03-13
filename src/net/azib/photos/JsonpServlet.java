package net.azib.photos;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.String;
// import java.util.Date;

public class JsonpServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String callback = req.getParameter("callback");
        if (callback == null) {
            callback = "callback";
        }
        Picasa picasa = new Picasa();
        req.setAttribute("gallery", picasa.getGallery());
        req.setAttribute("callback", callback);
        // req.setAttribute("num", num);
        req.getRequestDispatcher("/WEB-INF/jsp/gallery-jsonp.jsp").forward(req, resp);
        
        // Date date = new Date();
        // resp.setDateHeader("Last-Modified", date.getTime());
        // resp.setDateHeader("Expires", date.getTime() + 604800000);
        resp.setHeader("Cache-Control", "public, max-age=604800");
        
        // PrintWriter out = resp.getWriter();
        // resp.setContentType("text/javascript");
        // out.write(callback + "(" +  + ");");
        // out.close();
    }
}
