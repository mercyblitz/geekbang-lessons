package org.geektimes.rest.demo;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class HttpURLConnectionDemo {

    public static void main(String[] args) throws Throwable {
        URI uri = new URI("http://127.0.0.1:8080/hello/world");
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        try (InputStream inputStream = connection.getInputStream()) {
            System.out.println(IOUtils.toString(inputStream, "UTF-8"));
        }
        // 关闭连接
        connection.disconnect();
    }
}

