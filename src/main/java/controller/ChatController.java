package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

public class ChatController extends HttpServlet{
    final static Logger logger = Logger.getLogger(ChatController.class.getName());
    @Override
    public void init() throws ServletException {
        super.init(); //이건 왜필요한거지
        logger.info("i am ChatController init yeeeeee");
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
//        super.service(req, res);
        logger.info("i am Service yeeeeee");
        res.getWriter().println("I am Service yeeeeee");
    }
}

