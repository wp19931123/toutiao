package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import com.nowcoder.model.User;
import com.nowcoder.service.ToutiaoService;
import org.jboss.logging.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class IndexController  {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Autowired
    private ToutiaoService toutiaoService;

    @RequestMapping("/")
    @ResponseBody
    public  String index(HttpSession session) {
        logger.info("Visit Index:" + logger.toString());
        return "hello nowcoder," + session.getAttribute("msg") + "<br>say:" + toutiaoService.say();
    }

    @RequestMapping(value={"profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value = "type",defaultValue = "1") int type,
                          @RequestParam(value = "key",defaultValue = "nowcoder") String key) {
        return String.format("{%s},{%d},{%d},{%s}",groupId,userId,type,key);
    }
    @RequestMapping(value = {"/vm"})
    public String news(Model model) {
        model.addAttribute("value1","vv1");
        List<String> colors = Arrays.asList(new String[]{"RED","GREEN","YELLOW","BLUE"});

        Map<String,String> map = new HashMap<>();
        for(int i = 0;i < 4;i++) {
            map.put(String.valueOf(i),String.valueOf(i*i));
        }
        model.addAttribute("colors",colors);
        model.addAttribute("map",map);
        model.addAttribute("user",new User("jim"));

        return "news";

    }

    @RequestMapping(value={"/request"})
    @ResponseBody
    public String request(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        StringBuffer sb = new StringBuffer();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }

        for(Cookie cookie : request.getCookies()) {
            sb.append("Cookie:");
            sb.append(cookie.getName());
            sb.append(":");
            sb.append(cookie.getValue());
            sb.append("<br>");
        }
        sb.append("method:" + request.getMethod() + "<br>");
        sb.append("pathInfo:" +request.getPathInfo() + "<br>");
        sb.append("queryString:" +"method:" +request.getQueryString() + "<br>");
        sb.append("requestURI:" +request.getRequestURI() + "<br>");

        return sb.toString();
    }

    @RequestMapping(value={"/response"})
    @ResponseBody
    public String request(@CookieValue(value = "nowcoder", defaultValue = "a") String nowcoder,
                          @RequestParam(value="key", defaultValue = "key") String key,
                          @RequestParam(value = "value",defaultValue = "value") String value,
                          HttpServletResponse response) {
        response.addCookie(new Cookie(key,value));
        response.addHeader(key,value);
        return "NowCoderId From Cookie :" + nowcoder;
    }

    @RequestMapping("/redirect/{code}")
    @ResponseBody
    public RedirectView redirect(@PathVariable("code") int code){
        RedirectView red = new RedirectView("/",true);
        if(code == 301) {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;
    }
    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key",required = false) String key) {
        if("admin".equals(key)) {
            return "Hello Admin";
        }
        throw new IllegalArgumentException("key Error");
    }

    @ExceptionHandler
    @ResponseBody
    public String error(Exception e) {
        return "error:" + e.getMessage();
    }
}
