package com.vaadin.cdi.tutorial;

import java.io.Serializable;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.vaadin.cdi.access.AccessControl;

@Alternative
public class CustomAccessControl extends AccessControl implements Serializable {

    @Inject
    private UserInfo userInfo;

    @Override
    public boolean isUserSignedIn() {
        return userInfo.getUser() != null;
    }

    @Override
    public boolean isUserInRole(String role) {
        if (isUserSignedIn()) {
            for (String userRole : userInfo.getRoles()) {
                if (role.equals(userRole)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getPrincipalName() {
        if (isUserSignedIn()) {
            return userInfo.getUser().getUsername();
        }
        return null;
    }

}
