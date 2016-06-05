package net.coderodde.wikipedia.webapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.coderodde.wikipedia.sp.AbstractWikipediaShortestPathFinder;
import net.coderodde.wikipedia.sp.WikipediaURLHandler;
import net.coderodde.wikipedia.sp.support.ParallelBidirectionalWikipediaShortestPathFinder;

@WebServlet(name = "WikipediaPathSearchServlet", urlPatterns = {"/search"})
public class WikipediaPathSearchServlet extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String fromUrl = request.getParameter("from_url").trim();
        String toUrl   = request.getParameter("to_url")  .trim();

        // When deployed to Heroku, prints to the application log file.
        System.out.println("[WIKIPATH_QUERY] " + fromUrl + " -> " + toUrl);
        
        AbstractWikipediaShortestPathFinder finder = 
                new ParallelBidirectionalWikipediaShortestPathFinder();
        
        try {
            WikipediaURLHandler handlerFrom = new WikipediaURLHandler(fromUrl);
            WikipediaURLHandler handlerTo   = new WikipediaURLHandler(toUrl);
            
            if (!handlerFrom.getBasicURL().equals(handlerTo.getBasicURL())) {
                request.setAttribute("error_msg", 
                                     "The two given Wikipedia articles seem " +
                                     "to be belong to different languages.");
                request.getRequestDispatcher("show.jsp")
                       .forward(request, response);
                
                return;
            }
            
            List<String> path = finder.search(handlerFrom.getTitle(),
                                              handlerTo.getTitle(), 
                                              handlerTo.getAPIURL(),
                                              null);
            
            request.setAttribute(
                    "result_msg", 
                    String.format("The search took %.3f seconds, expanding " +
                                  "%d nodes.",
                                  finder.getDuration() / 1e3,
                                  finder.getNumberOfExpandedNodes()));
            
            List<ArticleData> ret = new ArrayList<>(path.size());
            
            for (String title : path) {
                ret.add(new ArticleData("https://" + handlerTo.getBasicURL() 
                                                   + WikipediaURLHandler.WIKI_DIR_TOKEN
                                                   + title,
                                        title));
            }
            
            request.setAttribute("solution", ret);
        } catch (Exception ex) {
            request.setAttribute("error_msg", ex.getMessage());
        }

        request.getRequestDispatcher("show.jsp").forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "This servlet is responsible for computing a shortest path " +
               "between two Wikipedia articles.";
    }
}
