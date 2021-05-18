package ru.otus.example.facadegateway.config;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.otus.example.facadegateway.security.MyUserPrincipal;

@Component
public class AuthenticationFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();

        SecurityContext securityContext = SecurityContextHolder.getContext();
        MyUserPrincipal userDetails = (MyUserPrincipal) securityContext.getAuthentication().getPrincipal();

        ctx.addZuulRequestHeader("userId", userDetails.getUser().getId());
        return null;
    }
}