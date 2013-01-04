/*
 *  Copyright 2009 Richard Nichols.
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
package com.visural.wicket.component.submitters;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import com.visural.wicket.component.submitters.impl.IndicateModalAsyncComponent;
import com.visural.wicket.component.submitters.impl.IndicateModalCommon;
import com.visural.wicket.security.IPrivilege;
import com.visural.wicket.security.ISecureEnableInstance;
import com.visural.wicket.security.ISecureRenderInstance;

/**
 * An AJAX `Link` which displays a modal dialog indicating work-in-progress, while the update occurs. *Requires JQuery*
 *
 * @version $Id: IndicateModalAjaxLink.java 261 2011-03-08 20:53:16Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public abstract class IndicateModalAjaxLink extends AjaxLink implements IndicateModalAsyncComponent, ISecureEnableInstance, ISecureRenderInstance {
    private static final long serialVersionUID = 1L;
    
    private final IndicateModalCommon common;
    private int timeoutMillis = 30000;

    public IndicateModalAjaxLink(String id) {
        super(id);
        common = new IndicateModalCommon(this);
    }

    /**
     * Override and return false to suppress static JavaScript and CSS contributions.
     * (May be desired if you are concatenating / compressing resources as part of build process)
     * @return
     */
    public boolean autoAddToHeader() {
        return true;
    }

    public void setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public int getTimeoutMillis() {
        return this.timeoutMillis;
    }

    /**
     * Override to enable BGI frame IE6 support.
     * @return
     */
    public boolean isSupportIE6() {
        return false;
    }

    /**
     * You may override the html that is inserted into the page to something
     * other than a simple AJAX indicator image.
     *
     * @return the html to be replaced for the given container component.
     */
    public String getIndicatorHTML() {
        return common.getDefaultIndicatorHTML();
    }

    /**
     * Override to change "timeout" text.
     * @return
     */
    public String getTimeoutHTML() {
        return common.getDefaultTimeoutHTML();
    }
    
    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);
        IAjaxCallListener myAjaxCallListener = common.getAjaxCallDecorator();
        attributes.getAjaxCallListeners().add(myAjaxCallListener);
    }

    public IPrivilege getRenderPrivilege() {
        return IPrivilege.NULL;
    }

    public IPrivilege getEnablePrivilege() {
        return IPrivilege.NULL;
    }

}
