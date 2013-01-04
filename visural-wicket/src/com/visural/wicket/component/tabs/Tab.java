/*
 *  Copyright 2010 Visural.
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
package com.visural.wicket.component.tabs;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import com.visural.wicket.security.IPrivilege;
import com.visural.wicket.security.ISecureEnableInstance;
import com.visural.wicket.security.ISecureRenderInstance;

/**
 *
 * @author Visural
 */
public class Tab extends WebMarkupContainer implements ISecureEnableInstance, ISecureRenderInstance {
    private static final long serialVersionUID = 1L;
    
    private final String label;
    private final IModel labelModel;

    public Tab(String id, IModel label) {
        super(id);
        this.label = null;
        labelModel = label;
        common();
    }
    
    public Tab(String id, String label) {
        super(id);
        this.label = label;
        labelModel = null;
        common();
    }

    private void common() {
        setOutputMarkupId(true);
    }

    public final boolean isSelected() {
        Tabs tabs = (Tabs) getParent();
        return (tabs.getSelected().equals(this));
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        tag.put("class", isSelected() ? "tabpage_selected" : "tabpage");
        super.onComponentTag(tag);
    }

    public String getLabel() {
        return label == null ? (String)labelModel.getObject() : label;
    }

    public IPrivilege getRenderPrivilege() {
        return IPrivilege.NULL;
    }

    public IPrivilege getEnablePrivilege() {
        return IPrivilege.NULL;
    }

}
