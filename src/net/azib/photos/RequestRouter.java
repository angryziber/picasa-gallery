package net.azib.photos;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.logging.Logger;

import static javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.xml.bind.DatatypeConverter.parseInt;

public class RequestRouter implements Filter {
  private static final Logger logger = Logger.getLogger(RequestRouter.class.getName());
  private ServletContext context;
  private static VelocityEngine velocity;

  public void init(FilterConfig config) throws ServletException {
    this.context = config.getServletContext();
    Properties velocityProps = new Properties();
    velocityProps.setProperty("file.resource.loader.path", context.getRealPath("/WEB-INF/views"));
    velocityProps.setProperty("file.resource.loader.cache", "true");
    velocity = new VelocityEngine(velocityProps);
    velocity.setApplicationAttribute("javax.servlet.ServletContext", context);
    velocity.init();
  }

  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
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
      request.setAttribute("servletPath", request.getServletPath());
      request.setAttribute("bot", bot);
      request.setAttribute("mobile", userAgent != null && userAgent.contains("Mobile") && !userAgent.contains("iPad") && !userAgent.contains("Tab"));

      if (random != null) {
        request.setAttribute("delay", request.getParameter("delay"));
        if (request.getParameter("refresh") != null) request.setAttribute("refresh", true);
        render("random", picasa.getRandomPhotos(parseInt(random.length() > 0 ? random : "1")), request, response);
      }
      else if (path == null || "/".equals(path)) {
        if (request.getParameter("reload") != null)
          new CacheReloader().reload();

        render("gallery", picasa.getGallery(), request, response);
      }
      else if (path.lastIndexOf('.') >= path.length() - 4) {
        chain.doFilter(req, resp);
      }
      else {
        String[] parts = path.split("/");
        Album album;
        try {
          album = picasa.getAlbum(parts[1]);
        }
        catch (MissingResourceException e) {
          album = picasa.search(parts[1]);
          album.title = "Photos matching '" + parts[1] + "'";
          // TODO: no longer works for non-logged-in requests
        }

        if (parts.length > 2) {
          for (Photo photo : album.photos) {
            if (photo.id.equals(parts[2])) {
              request.setAttribute("photo", photo);
              break;
            }
          }
        }
        render("album", album, request, response);
      }
    }
    catch (MissingResourceException e) {
      response.sendError(SC_NOT_FOUND);
    }
  }

  static boolean isBot(String userAgent) {
    return userAgent == null || userAgent.toLowerCase().contains("bot/") || userAgent.contains("spider/");
  }

  static void render(String template, Object source, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    long start = System.currentTimeMillis();

    request.setAttribute(template, source);

    if (response.getContentType() == null)
      response.setContentType("text/html; charset=utf8");
    if (source instanceof Entity && ((Entity) source).timestamp != null)
      response.addDateHeader("Last-Modified", ((Entity) source).timestamp);

    VelocityContext ctx = new VelocityContext();
    Enumeration<String> attrs = request.getAttributeNames();
    while (attrs.hasMoreElements()) {
      String name =  attrs.nextElement();
      ctx.put(name, request.getAttribute(name));
    }
    Template tmpl = velocity.getTemplate(template + ".vm");
    tmpl.merge(ctx, response.getWriter());

    logger.info("Rendered in " + (System.currentTimeMillis() - start) + " ms");
  }

  public void destroy() {
  }
}
