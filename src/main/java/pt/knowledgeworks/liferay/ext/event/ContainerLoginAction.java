/*
 * Copyright by KnowledgeWorks. All rights reserved.
 *
 * This software is the proprietary information of KnowledgeWorks
 * Use is subject to license terms under the Apache License 2.0.
 *
 * http://www.knowledgeworks.pt
 */
package pt.knowledgeworks.liferay.ext.event;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;

/**
 * Liferay Pre, Post action event handler that perform authentication on Servlet 3 container calling HttpServletRequest.login method passing credentials during
 * login<br>
 * This action is intended avoid configuration of "portal.jaas.enable", with 
 * will perform additional redirect in order to perform authentication in Servlet
 * container<br>
 *
 * The Liferay user should be found in session under for key {@link com.liferay.portal.kernel.util.WebKeys#USER}
 * Credential is assumed to be present on session key {@link com.liferay.portal.kernel.util.WebKeys#USER_PASSWORD}
 * , if not found is obtained from {@link com.liferay.portal.model.User#getPassword"}<br>
 * If some error happens during authentication in web container a warning is logged in 
 * log indicating that user was not authenticated against container
 *
 */
public class ContainerLoginAction extends Action {
    private static final Log LOG = LogFactoryUtil.getLog(ContainerLoginAction.class);

    @Override
    public void run(HttpServletRequest req, HttpServletResponse res) throws ActionException {
        HttpSession session = req.getSession();

        User user = (User) session.getAttribute(WebKeys.USER);
        String password = (String) session.getAttribute(WebKeys.USER_PASSWORD);
        boolean isSessionPassword = true;

        if (user != null) {
            try {
                String username = user.getLogin();
                if (password == null) {
                    isSessionPassword = false;
                    password = user.getPassword();
                }
                try {
                    req.login(username, password);
                    LOG.info("CAUTH0001: User '" + username + "' logged on container");
                    if (GetterUtil.get(PropsUtil.get("ext.remove.session.store.password"), false)) {
                        session.removeAttribute(WebKeys.USER_PASSWORD);
                    }
                } catch (ServletException ex) {
                    String message = "CAUTH0010: Unable to perform container login for user=" + username + "; isSessionPassword=" + isSessionPassword;
                    LOG.warn(message + " :: " + ex);
                    if (LOG.isTraceEnabled()) {
                        LOG.trace(message, ex);
                    }
                }
            } catch (PortalException | SystemException ex) {
                String message = "CAUTH0011: Unxpected error while retring information on user " + user;
                LOG.warn(message + " :: " + ex);
                throw new ActionException(message, ex);
            }
        } else {
            String message = "CAUTH0012: User not found in session";
            LOG.warn(message);
            throw new ActionException(message);
        }
    }
}
