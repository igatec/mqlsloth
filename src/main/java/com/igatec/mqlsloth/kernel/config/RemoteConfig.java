package com.igatec.mqlsloth.kernel.config;

import java.util.Objects;

public class RemoteConfig {

    private String name;
    private String appUrl;
    private String userName;
    private String password;

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean validate() {
        return name != null && appUrl != null && userName != null && password != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteConfig that = (RemoteConfig) o;
        return Objects.equals(appUrl, that.appUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appUrl);
    }
}
