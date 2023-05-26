package com.h4p3.context;

import com.h4p3.exception.AccessDeniedException;
import org.springframework.util.Assert;

public class RequestContextHolder {

    private static final ThreadLocal<RequestContext> contextHolder = new ThreadLocal<>();

    public static void clearContext() {
        contextHolder.remove();
    }

    public static RequestContext getContext() {
        RequestContext ctx = contextHolder.get();
        if (ctx == null) {
            throw new AccessDeniedException("请登录");
        }
        return ctx;
    }

    public static void setContext(RequestContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        contextHolder.set(context);
    }

}
