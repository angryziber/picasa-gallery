package net.azib.photos;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestRouter implements Filter {
    Picasa picasa = new Picasa();

    public void init(FilterConfig config) throws ServletException {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String agent = request.getHeader("User-Agent");
        String path = request.getRequestURI();

        if (agent != null && agent.contains("Mobile")) {
            // redirect mobile users to the real Picasaweb
            response.sendRedirect("http://picasaweb.google.com/" + Picasa.USER);
        }
        else if (path == null || "/".equals(path)) {
            request.setAttribute("gallery", picasa.getGallery());
            render("gallery", request, response);
        }
        else {
            String[] parts = path.split("/");
            request.setAttribute("album", picasa.getAlbum(parts[1]));
            request.setAttribute("photoId", parts.length > 2 ? parts[2] : null);
            render("album", request, response);
        }
    }

    void render(String jsp, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setAttribute("picasa", picasa);
        response.setContentType("text/html; charset=utf8");
        request.getRequestDispatcher("WEB-INF/jsp/" + jsp + ".jsp").include(request, response);
    }

    public void destroy() {
    }
}
