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

package com.visural.wicket.component.confirmer;

import com.visural.wicket.component.confirmer.impl.ConfirmerCommon;
import com.visural.wicket.component.confirmer.impl.ConfirmerComponent;
import com.visural.wicket.component.confirmer.impl.ConfirmerIcon;
import com.visural.wicket.security.IPrivilege;
import com.visural.wicket.security.ISecureEnableInstance;
import com.visural.wicket.security.ISecureRenderInstance;

import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.ajax.markup.html.AjaxLink;

/**
 * @version $Id: ConfirmerAjaxLink.java 261 2011-03-08 20:53:16Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public abstract class ConfirmerAjaxLink extends AjaxLink implements ConfirmerComponent, ISecureEnableInstance, ISecureRenderInstance {
    private static final long serialVersionUID = 1L;
    
    private final ConfirmerCommon common;
    
    public ConfirmerAjaxLink(String id) {
        super(id);
        common = new ConfirmerCommon(this);
    }

    /**
     * Override and return false to suppress static JavaScript and CSS contributions.
     * (May be desired if you are concatenating / compressing resources as part of build process)
     * @return
     */
    public boolean autoAddToHeader() {
        return true;
    }

    /**
     * Override this method to customize the look and feel of the generated links.
     *
     * @param buttonLabel
     * @param onClickJS
     * @return
     */
    public String getLinkHTML(String buttonLabel, String onClickJS) {
        return common.getLinkHTML(buttonLabel, onClickJS);
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
    	super.updateAjaxAttributes(attributes);
    	
    	if(isDisplayConfirmation()) 
    	{
    		attributes.getAjaxCallListeners().add(common.getAjaxCallDecorator());
    	}
    }
    
    /**
     * Override to disable the confirmation prompt in some situations (only at render time).
     */
    protected boolean isDisplayConfirmation() {
        return true;
    }

    public ConfirmerAjaxLink setOKButtonLabel(String label) {
        common.setOkButtonLabel(label);
        return this;
    }

    public ConfirmerAjaxLink setCancelButtonLabel(String label) {
        common.setCancelButtonLabel(label);
        return this;
    }

    public ConfirmerAjaxLink setOKButtonDisplayed(boolean display) {
        common.setDisplayOK(display);
        return this;
    }

    public ConfirmerAjaxLink setCancelButtonDisplayed(boolean display) {
        common.setDisplayCancel(display);
        return this;
    }

    public ConfirmerAjaxLink setIcon(ConfirmerIcon icon) {
        common.setIcon(icon);
        return this;
    }

    public String getCancelButtonLabel() {
        return common.getCancelButtonLabel();
    }

    public boolean isDisplayCancel() {
        return common.isDisplayCancel();
    }

    public boolean isDisplayOK() {
        return common.isDisplayOK();
    }

    public ConfirmerIcon getIcon() {
        return common.getIcon();
    }

    public String getOkButtonLabel() {
        return common.getOkButtonLabel();
    }

    public boolean isSupportIE6() {
        return false;
    }

    public String getMessageContentHTML() {
        return common.getMessageContentHTML();
    }

    public ConfirmerAjaxLink setMessageContentHTML(String messageContentHTML) {
        common.setMessageContentHTML(messageContentHTML);
        return this;
    }

    public ConfirmerAjaxLink setDialogWidth(int width) {
        common.setModalWidth(width);
        return this;
    }

    public IPrivilege getRenderPrivilege() {
        return IPrivilege.NULL;
    }

    public IPrivilege getEnablePrivilege() {
        return IPrivilege.NULL;
    }

}
