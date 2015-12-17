package com.fujixerox.aus.asset.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.fujixerox.aus.asset.api.resource.IResourceResolver;
import com.fujixerox.aus.asset.api.util.cache.impl.ThreadLocalStorage;
import com.fujixerox.aus.asset.web.resource.ContextResourceResolver;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class ThreadLocalCacheFilter implements Filter {

    private IResourceResolver _resolver;

    public ThreadLocalCacheFilter() {
        super();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        try {
            setupResolver();
            chain.doFilter(request, response);
        } finally {
            ThreadLocalStorage.clear();
        }
    }

    private void setupResolver() {
        ThreadLocalStorage.put(IResourceResolver.class, _resolver);
    }

    @Override
    public void destroy() {
        _resolver = null;
        ThreadLocalStorage.setActive(false);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        _resolver = new ContextResourceResolver(filterConfig
                .getServletContext());
        ThreadLocalStorage.setActive(true);
    }

}
