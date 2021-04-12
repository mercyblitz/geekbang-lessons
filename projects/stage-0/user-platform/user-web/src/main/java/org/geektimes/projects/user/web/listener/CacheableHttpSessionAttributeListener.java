package org.geektimes.projects.user.web.listener;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

public class CacheableHttpSessionAttributeListener
        implements HttpSessionAttributeListener {

    // Cache Key -> session id + attribute name

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        //
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {

    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {

    }
}
