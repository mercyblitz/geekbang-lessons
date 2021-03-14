package org.geektimes.projects.user.management;

import javax.management.MXBean;

@MXBean
public interface AddressInterface {

    String getPro();

    void setPro(String pro);

    String getArea();

    void setArea(String area);
}
