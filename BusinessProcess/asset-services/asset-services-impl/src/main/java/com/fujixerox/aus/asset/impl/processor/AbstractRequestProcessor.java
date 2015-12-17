package com.fujixerox.aus.asset.impl.processor;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.Subject;

import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.fujixerox.aus.asset.api.dfc.credentials.ICredentialManager;
import com.fujixerox.aus.asset.api.dfc.credentials.IDocumentumCredentials;
import com.fujixerox.aus.asset.api.dfc.session.IDfcSessionInvoker;
import com.fujixerox.aus.asset.api.dfc.util.SessionUtils;
import com.fujixerox.aus.asset.api.processor.IRequestProcessor;
import com.fujixerox.aus.asset.api.util.logger.Logger;
import com.fujixerox.aus.asset.impl.constants.ResponseConstants.GenericCode;
import com.fujixerox.aus.asset.impl.dfc.cache.SessionManagerCache;
import com.fujixerox.aus.asset.model.beans.generated.request.RequestBase;
import com.fujixerox.aus.asset.model.beans.generated.response.ResponseBase;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public abstract class AbstractRequestProcessor<I extends RequestBase, O extends ResponseBase>
        implements IRequestProcessor<I, O> {

    private final ICredentialManager _credentialManager;

    private final boolean _authenticationRequired;

    protected AbstractRequestProcessor(ICredentialManager credentialManager,
            boolean authenticationRequired) {
        super();
        _credentialManager = credentialManager;
        _authenticationRequired = authenticationRequired;
    }

    @Override
    public final boolean canProcess(I request) {
        if (getRequestClass().isAssignableFrom(request.getClass())) {
            return doCanProcess(getRequestClass().cast(request));
        }
        return false;
    }

    protected abstract boolean doCanProcess(I request);

    protected abstract O doProcess(I request, String userName);

    protected boolean isAuthenticationRequired() {
        return _authenticationRequired;
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public O process(I request, Map headers) {
        try {
            if (!canProcess(request)) {
                Logger.error("Can't process request: " + request);
                return syntaxError(request);
            }
            if (isAuthenticationRequired() && !checkAuthenticated(request)) {
                return invalidSecurityToken(request);
            }
            return doProcess(getRequestClass().cast(request),
                    extractUserInfo(headers));
        } catch (Error error) {
            Logger.error(error.getMessage(), error);
            throw error;
        } catch (Throwable throwable) {
            Logger.error(throwable.getMessage(), throwable);
            return handleThrowable(request, throwable);
        }
    }

    private O handleThrowable(I request, Throwable throwable) {
        return createResponse(request).<O> withResponseCode(
                GenericCode.SYNTAX_ERROR).withSessionInfo(
                request.getSessionInfo());
    }

    private boolean checkAuthenticated(I request) {
        String token = request.getSecurityToken();
        if (StringUtils.isBlank(token)) {
            Logger.error("Empty sectoken");
            return false;
        }
        Subject subject = getSubject(request);
        boolean result = subject.isAuthenticated();
        if (!result) {
            Logger.error("No session for sectoken "
                    + request.getSecurityToken());
        }
        return result;
    }

    protected Subject getSubject(I request) {
        return new Subject.Builder().sessionId(request.getSecurityToken())
                .buildSubject();
    }

    protected O createResponse(I request) {
        try {
            return getResponseClass().newInstance().withSessionInfo(
                    request.getSessionInfo());
        } catch (IllegalAccessException | InstantiationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private O invalidSecurityToken(I request) {
        return createResponse(request).<O> withResponseCode(
                GenericCode.INVALID_TOKEN).withSessionInfo(
                request.getSessionInfo());
    }

    private O syntaxError(I request) {
        return createResponse(request).<O> withResponseCode(
                GenericCode.SYNTAX_ERROR).withSessionInfo(
                request.getSessionInfo());
    }

    protected final <T> T invokeInSession(String sessionId,
            IDfcSessionInvoker<T> invoker) throws DfException {
        IDfSessionManager sessionManager = null;
        IDocumentumCredentials credentials;
        if (isAuthenticationRequired()) {
            credentials = _credentialManager.getCredentials(sessionId);
        } else {
            credentials = _credentialManager.getCredentials(null, null);
        }
        try {
            sessionManager = SessionManagerCache.getSessionManager(credentials);
            return SessionUtils.invokeInNewSession(sessionManager, credentials
                    .getDocbaseName(), invoker);
        } finally {
            if (sessionManager != null) {
                SessionManagerCache.releaseSessionManager(credentials,
                        sessionManager);
            }
        }
    }

    private String extractUserInfo(Map headers) {
        String auth = (String) headers.get("authorization");
        if (StringUtils.isNotBlank(auth)) {
            return auth;
        }
        auth = (String) headers.get("referer");
        if (StringUtils.isNotBlank(auth)) {
            String[] parts = auth.split("/");
            if (parts.length != 3) {
                return null;
            }
            return parts[1];
        }
        return null;
    }

}
