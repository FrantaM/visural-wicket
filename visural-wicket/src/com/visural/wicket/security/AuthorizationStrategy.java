/*
 *  Copyright 2010 Richard Nichols.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package com.visural.wicket.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;

/**
 * Authorization strategy that enables components implementing {@link ISecureEnableInstance}
 * and {@link ISecureRenderInstance} to work securely.
 *
 * Customize your Wicket `Application` class to use this authorization strategy.
 *
 * @version $Id: AuthorizationStrategy.java 261 2011-03-08 20:53:16Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class AuthorizationStrategy implements IAuthorizationStrategy {
    private static final long serialVersionUID = 1L;

    private final IClientProvider clientProvider;
    private final Map<Class,IPrivilege> createPrivilege = Collections.synchronizedMap(new HashMap<Class,IPrivilege>());

    /**
     * Create a new instance of AuthorizationStrategy.
     *
     * You must supply a suitable IClientProvider.
     *
     * @param clientProvider
     */
    public AuthorizationStrategy(IClientProvider clientProvider) {
        this.clientProvider = clientProvider;
    }

    public <T extends IRequestableComponent> boolean isInstantiationAuthorized(Class<T> type) {
        if (createPrivilege.get(type) != null) {
            return createPrivilege.get(type).isGrantedToClient(clientProvider.getCurrentClient());
        }
        return true;
    }

    public <T extends Component> void setCreatePrivilege(Class<T> type, IPrivilege priv) {
        createPrivilege.put(type, priv);
    }

    // TODO: check for null privilege and raise IllegalStateException
    public boolean isActionAuthorized(Component com, Action action) {
        if (action.equals(Component.ENABLE) && ISecureEnableInstance.class.isAssignableFrom(com.getClass())) {
            if (!((ISecureEnableInstance)com).getEnablePrivilege().isGrantedToClient(clientProvider.getCurrentClient())) {
                return false;
            }
        }
        if (action.equals(Component.RENDER) && ISecureRenderInstance.class.isAssignableFrom(com.getClass())) {
            if (!((ISecureRenderInstance)com).getRenderPrivilege().isGrantedToClient(clientProvider.getCurrentClient())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isResourceAuthorized(IResource ir, PageParameters pp) {
        return true;
    }

}
