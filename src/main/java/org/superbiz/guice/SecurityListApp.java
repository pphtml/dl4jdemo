package org.superbiz.guice;

import org.superbiz.dao.SecurityDAO;

import javax.inject.Inject;

public class SecurityListApp {
    @Inject
    SecurityDAO securityDAO;

    public void list() {
        securityDAO.findAll();
        securityDAO.findAll();
    }
}
