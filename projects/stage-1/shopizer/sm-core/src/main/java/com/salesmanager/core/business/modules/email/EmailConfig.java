package com.salesmanager.core.business.modules.email;

import com.salesmanager.core.business.management.ManageableEmailConfig;
import org.json.simple.JSONObject;

import javax.management.openmbean.CompositeData;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class EmailConfig implements EmailConfigMBean {

    private String host;
    private String port;
    private String protocol;
    private String username;
    private String password;
    private boolean smtpAuth = false;
    private boolean starttls = false;

    private String emailTemplatesPath = null;

    @SuppressWarnings("unchecked")
    @Override
    public String toJSONString() {
        JSONObject data = new JSONObject();
        data.put("host", this.getHost());
        data.put("port", this.getPort());
        data.put("protocol", this.getProtocol());
        data.put("username", this.getUsername());
        data.put("smtpAuth", this.isSmtpAuth());
        data.put("starttls", this.isStarttls());
        data.put("password", this.getPassword());
        return data.toJSONString();
    }


    @Override
    public boolean isSmtpAuth() {
        return smtpAuth;
    }

    @Override
    public void setSmtpAuth(boolean smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    @Override
    public boolean isStarttls() {
        return starttls;
    }

    @Override
    public void setStarttls(boolean starttls) {
        this.starttls = starttls;
    }

    @Override
    public void setEmailTemplatesPath(String emailTemplatesPath) {
        this.emailTemplatesPath = emailTemplatesPath;
    }

    @Override
    public String getEmailTemplatesPath() {
        return emailTemplatesPath;
    }


    @Override
    public String getHost() {
        return host;
    }


    @Override
    public void setHost(String host) {
        this.host = host;
    }


    @Override
    public String getPort() {
        return port;
    }


    @Override
    public void setPort(String port) {
        this.port = port;
    }


    @Override
    public String getProtocol() {
        return protocol;
    }


    @Override
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    @Override
    public String getUsername() {
        return username;
    }


    @Override
    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public String getPassword() {
        return password;
    }


    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    public static EmailConfig from(CompositeData compositeData) throws Exception {
        EmailConfig emailConfig = new EmailConfig();
        BeanInfo beanInfo = Introspector.getBeanInfo(emailConfig.getClass(), Object.class);
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = compositeData.get(propertyName);
            Method method = propertyDescriptor.getWriteMethod();
            method.invoke(emailConfig, propertyValue);
        }
        return emailConfig;
    }

}
