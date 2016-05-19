package net.azib.photos;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.Source;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.CommentEntry;
import com.google.gdata.data.photos.GphotoEntry;
import com.google.gdata.util.ResourceNotFoundException;
import com.google.gdata.util.ServiceException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static java.util.Collections.emptyList;
import static javax.servlet.http.HttpServletResponse.*;
import static javax.xml.bind.DatatypeConverter.parseInt;

public class RequestRouter implements Filter {
  private static final Logger logger = Logger.getLogger(RequestRouter.class.getName());
  private ServletContext context;

  public void init(FilterConfig config) throws ServletException {
    this.context = config.getServletContext();
    // TODO: this doesn't work on AppEngine (would only work in backend)
    // ThreadManager.createBackgroundThread(new CacheReloader()).start();
  }

  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
    long start = System.currentTimeMillis();

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) resp;
    String path = request.getServletPath();

    String by = request.getParameter("by");
    String random = request.getParameter("random");
    String userAgent = request.getHeader("User-Agent");
    boolean bot = isBot(userAgent);
    if (bot && (by != null || random != null)) {
      response.sendRedirect("/");
      response.setStatus(SC_MOVED_PERMANENTLY);
      return;
    }

    try {
      Picasa picasa = new Picasa(by, request.getParameter("authkey"));
      request.setAttribute("picasa", picasa);
      request.setAttribute("host", request.getHeader("host"));
      request.setAttribute("bot", bot);
      request.setAttribute("mobile", userAgent.contains("Mobile") && !userAgent.contains("iPad") && !userAgent.contains("Tab"));

      if (random != null) {
        request.setAttribute("delay", request.getParameter("delay"));
        if (request.getParameter("refresh") != null) request.setAttribute("refresh", true);
        render("random", picasa.getRandomPhotos(parseInt(random.length() > 0 ? random : "1")), request, response);
      }
      else if (path == null || "/".equals(path)) {
        render("gallery", picasa.getGallery(), request, response);
      }
      else if (path.lastIndexOf('.') >= path.length() - 4) {
        chain.doFilter(req, resp);
      }
      else {
        String[] parts = path.split("/");
        AlbumFeed album;
        List<CommentEntry> comments = emptyList();
        try {
          album = picasa.getAlbum(parts[1]);
          comments = album.getEntries(CommentEntry.class);
        }
        catch (ResourceNotFoundException e) {
          album = picasa.search(parts[1]);
          album.setTitle(new PlainTextConstruct("Photos matching '" + parts[1] + "'"));
        }

        if (parts.length > 2) {
          for (GphotoEntry photo : album.getPhotoEntries()) {
            if (photo.getGphotoId().equals(parts[2])) {
              request.setAttribute("photo", photo);
              break;
            }
          }
        }
        request.setAttribute("comments", comments);
        render("album", album, request, response);
      }
    }
    catch (ResourceNotFoundException e) {
      response.sendError(SC_NOT_FOUND, e.getResponseBody());
    }
    catch (ServiceException e) {
      context.log("GData", e);
      response.sendError(SC_INTERNAL_SERVER_ERROR, e.getResponseBody());
    }
  }

  static boolean isBot(String userAgent) {
    return userAgent == null || userAgent.toLowerCase().contains("bot/") || userAgent.contains("spider/");
  }

  void render(String template, Object source, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    long start = System.currentTimeMillis();

    request.setAttribute(template, source);

    response.setContentType("text/html; charset=utf8");
    if (source instanceof Source)
      response.addDateHeader("Last-Modified", ((Source) source).getUpdated().getValue());

    request.getRequestDispatcher("/WEB-INF/jsp/" + template + ".jsp").include(request, response);

    logger.info("Rendered in " + (System.currentTimeMillis() - start) + " ms");
  }

  public void destroy() {
  }
}
