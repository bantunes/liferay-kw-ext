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

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Liferay Pre, Post action event handler that perform logout on Servlet 3 container calling HttpServletRequest.logout<br>
 */
public class ContainerLogoutAction extends Action {

    @Override
    public void run(HttpServletRequest req, HttpServletResponse res) throws ActionException {
        try {
            req.logout();
        } catch (ServletException ex) {
            final Log log = LogFactoryUtil.getLog(ContainerLogoutAction.class);
            final String msg = "CAUTH0020: Unxpected error while performing logout";
            log.warn(msg + " :: " + ex);
            if (log.isTraceEnabled()) {
                log.trace(msg, ex);
            }
        }
    }
}
