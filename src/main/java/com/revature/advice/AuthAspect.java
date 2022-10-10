package com.revature.advice;

import com.revature.annotations.AuthRestriction;

import com.revature.annotations.Authorized;
import com.revature.exceptions.NotLoggedInException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Aspect
@Component
public class AuthAspect {

    private final HttpServletRequest req;

    public AuthAspect(HttpServletRequest req) {
        this.req = req;
    }


    @Around("@annotation(authorized)")
    public Object authenticate(ProceedingJoinPoint pjp, Authorized authorized) throws Throwable {

        HttpSession session = req.getSession(); // Get the session (or create one)

        // If the user is not logged in
        if(session.getAttribute("user") == null) {
            throw new NotLoggedInException("Must be logged in to perform this action");
        }

        return pjp.proceed(pjp.getArgs()); // Call the originally intended method
    }
}
