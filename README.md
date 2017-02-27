# LIferay Portal KW Extentions

A set of utilities for Liferay Portal

## ContainerLogin

A Liferay Post event action handler that perform authentication on Servlet 3 container
calling [HttpServletRequest.login](http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#login(java.lang.String,%20java.lang.String)) method passing users credentials

This action is intended avoid configuration of "portal.jaas.enable", with will perform additional redirect
in order to perform authentication in Servlet container.

A security domain / realm should be configured on web container associated with the Liferay Portal WebApp.

When in using this functionality Liferay Portal is normally configured to authenticate users
on external directory (such and LDAP Server). On Application Server we should perform necessary
configurations (according to Application Server in order to configure JAAS LoginModule to autenticate user
in same directory.

Notes: Tested with JBoss 7.5.* and WebLogic Server 12.1.2.*


### Configuration
1. Add to the liferay property "login.events.post" the class ContainerLoginAction to the pipeline

Example:
```
login.events.post=pt.knowledgeworks.liferay.ext.event.ContainerLoginAction,com.liferay.portal.events.ChannelLoginPostAction,com.liferay.portal.events.DefaultLandingPageAction,com.liferay.portal.events.LoginPostAction
```

2. [optional] Add to the liferay property "logout.events.post" the class ContainerLogoutAction to the pipeline
Example:
```
logout.events.post=com.liferay.portal.events.LogoutPostAction,com.liferay.portal.events.DefaultLogoutPageAction,com.liferay.portal.events.SiteMinderLogoutAction,pt.knowledgeworks.liferay.ext.event.ContainerLooutAction
```

**Info**
The ContainerLogin will use the credentials stored clear text in session if portal property 'session.store.password' is set to 'true'; if credential is not found it used the user credential associated with liferay user; with can be encrypted.

Being a pre or post event If error happens authentication is not aborted; messages are logged in order do indicate if authentication was performed in container with INFO level
'''
CAUTH0001: User 'username' logged on container
'''

If authentication is not succeeded following message is logged with WARN level

'''
CAUTH0010: Unable to perform container login for user='username'; isSessionPassword='isSessionPassword'
''''
Note: The isSessionPassword is a flag signaling if password was obtained from ession (in clear text) or from user (wth can be encrypted)

### Optional Configuration
If liferay property "ext.remove.session.store.password" is set to true; the session attribute 'session.store.password' is removed after sucesefull login on container.

# Build
In order to perform build need Java SE 7 and Maven 3. Perform build project with command mvn clean install

# Installation
Copy the jar 'liferay-kw-ext\<version\>'.jar to liferay portal webapp under WEB-INF/lib


### Notes on Liferay Login Modules

Liferay Portal comes with JAAS PortalLoginModule that can be used to authenticated users in container
This implementation is mainly intended to be used in portal and accepts and encrypted password
when performing authentication.

When performing security configurations on Portal and other WebaApps, the Login Module should 
be changed to used external LDAP LoginModule.

### Notes on JBoss

If using JBoss or WildFly its also necessary for enable SSO between Web Applications by default this is disabled.
See: [Use Single Sign On (SSO) In A Web Application](https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/6.2/html/Security_Guide/Use_Single_Sign_On_SSO_In_A_Web_Application.html)

For "simple" standalone configurations, with default virtual host, simple jboss-cli, connect and type:
'''
/subsystem=web/virtual-server=default-host/sso=configuration:add(reauthenticate="false")
'''

### Notes on WebLogic Server
 
On WebLogic Server, if using same session cookie; by default this SSO enabled between web applications.
I using several WLS domain do not forget to [Enabling Trust Between WebLogic Server Domains](https://docs.oracle.com/cd/E24329_01/web.1211/e24422/domain.htm#SECMG404)

