package org.geektimes.projects.user.management;

public class Address implements AddressInterface{
    private String pro;
    private String area;

    @Override
    public String getPro() {
        return pro;
    }

    @Override
    public void setPro(String pro) {
        this.pro = pro;
    }

    @Override
    public String getArea() {
        return area;
    }

    @Override
    public void setArea(String area) {
        this.area = area;
    }
}
