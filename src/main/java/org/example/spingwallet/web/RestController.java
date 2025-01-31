package org.example.spingwallet.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.web.bind.annotation.RestController
@RequestMapping ("/api/v1")
public class RestController {

    @GetMapping("/info/1")
    public String getInfo ( HttpServletRequest request, HttpServletResponse response ) {

        String author = request.getHeader("Author");
        response.addHeader("Application-name", "Spring-Wallet");

        Cookie cookie = new Cookie("color", "Orange");
        response.addCookie(cookie);


        return "Hello World!" + author;
    }

    @GetMapping("/info/2")
    public String getInfoMain (@CookieValue (value = "color")String color,HttpServletResponse response ) {



        return color;
    }
}
