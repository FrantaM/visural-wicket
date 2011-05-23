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
package com.visural.wicket.security.components;

import com.visural.wicket.security.IPrivilege;
import com.visural.wicket.security.ISecureEnableInstance;
import com.visural.wicket.security.ISecureRenderInstance;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

/**
 *
 * @author Richard Nichols
 */
public class SecureWebMarkupContainer extends WebMarkupContainer implements ISecureEnableInstance, ISecureRenderInstance {
    private static final long serialVersionUID = 1L;
    
    public SecureWebMarkupContainer(final String id) {
        super(id);
    }

    public SecureWebMarkupContainer(final String id, IModel<?> model) {
        super(id, model);
    }

    public IPrivilege getEnablePrivilege() {
        return IPrivilege.NULL;
    }

    public IPrivilege getRenderPrivilege() {
        return IPrivilege.NULL;
    }
}
