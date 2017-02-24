# LIferay Portal KW Extentions

A set of utilities for Liferay Portal

## ContainerLogin

A Liferay Post event action handler that perform authentication on Servlet 3 container
calling 'HttpServletRequest.login(...)' method passing users credentials

This action is intended avoid configuration of "portal.jaas.enable", with will perform additional redirect
in order to perform authentication in Servlet container.

A security realm should be configured on web container associated with the Liferay Portal WebApp

### Configuration
Add to the liferay property "login.events.post" the class ContainerLoginAction to the pipeline

Example:
```
login.events.post=pt.knowledgeworks.liferay.ext.event.ContainerLoginAction,com.liferay.portal.events.ChannelLoginPostAction,com.liferay.portal.events.DefaultLandingPageAction,com.liferay.portal.events.LoginPostAction
```

The ContainerLogin will use the credentials stored clear text in session if portal property 'session.store.password' is set to 'true'; if credential is not found it used the user credential associated with liferay user; with can be encrypted.

Being a pre os post event If error happens authentication is not aborted; messages are logged in order do indicate if authentication was performed in container with INFO level
'''
CAUTH0001: User 'username' logged on container
'''

If autentication is not suceeded following message is logged with WARN level

'''
CAUTH0010: Unable to perform container login for user='username'; isSessionPassword='isSessionPassword'
''''
Note: The isSessionPassword is a flag siganling if password was obtained from sesession (in clear text) or from user (wth can be encrypted)

### Optional Configuration
If liferay property "ext.remove.session.store.password" is set to true; the session attribute 'session.store.password' is removed after sucesefull login on container.

# Build
In order to perform build need Java SE 7 and Maven 3. Perform build project with ocmmand mvn clean install

# Installation
Copy the jar 'liferay-kw-ext\<version\>'.jar to liferay portal webapp under WEB-INF/lib
