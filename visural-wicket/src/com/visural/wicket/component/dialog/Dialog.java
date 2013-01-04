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
package com.visural.wicket.component.dialog;

import com.jquery.JQueryBGIFrameResourceReference;
import com.visural.javascript.JQueryCenterResourceReference;
import com.visural.wicket.component.submitters.impl.ModalCSSRef;
import com.visural.wicket.security.IPrivilege;
import com.visural.wicket.security.ISecureEnableInstance;
import com.visural.wicket.security.ISecureRenderInstance;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * A modal dialog box which appears over other content.
 *
 * The dialog can be opened or closed by straight JavaScript or by a
 * Wicket AjaxRequestTarget.
 *
 * It can optionally be closed by clicking outside the dialog.
 *
 * @author Richard Nichols
 */
public class Dialog extends WebMarkupContainer implements ISecureEnableInstance, ISecureRenderInstance {
    private static final long serialVersionUID = 1L;
    
    private boolean clickBkgToClose = false;

    public Dialog(String id) {
        super(id);
        setOutputMarkupId(true);
        add(new Behavior() {

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                response.render(OnDomReadyHeaderItem.forScript(getJS()));
            }

            private String getJS() {
                StringBuilder sb = new StringBuilder();
                sb.append("if (jQuery('#")
                  .append(getDivId())
                  .append("').length == 0) { jQuery(document.body).append('")
                  .append(getDiv().replace("'", "\\'"))
                  .append("'); }");
                return sb.toString();
            }

            private String getDivId() {
                return getMarkupId() + "_ovl";
            }

            private String getDiv() {
                if (isClickBkgToClose()) {
                    return ("<div id=\"" + getDivId() + "\" class=\"visuraloverlay\" onclick=\""+getCloseString()+"\"></div>");
                } else {
                    return ("<div id=\"" + getDivId() + "\" class=\"visuraloverlay\"></div>");
                }
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (autoAddToHeader()) {
        	response.render(JavaScriptReferenceHeaderItem.forReference(new JQueryCenterResourceReference()));
            response.render(CssHeaderItem.forReference(new ModalCSSRef()));
            if (isSupportIE6()) {
            	response.render(JavaScriptReferenceHeaderItem.forReference(new JQueryBGIFrameResourceReference()));
            }
        }
    }



    /**
     * Override and return false to suppress static JavaScript and CSS contributions.
     * (May be desired if you are concatenating / compressing resources as part of build process)
     * @return
     */
    protected boolean autoAddToHeader() {
        return true;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        tag.put("class", "visuraloverlaycontent");
    }

    /**
     * Open using the current Ajax context.
     * @param target
     */
    public void open(AjaxRequestTarget target) {
        target.appendJavaScript(getOpenString());
    }

    /**
     * Close using the current Ajax context.
     * @param target
     */
    public void close(AjaxRequestTarget target) {
        target.appendJavaScript(getCloseString());
    }

    /**
     * Return a Behavior which when applied to a Component will add an "onlick"
     * event handler that will open this Dialog.
     * @return
     */
    public Behavior getClickToOpenBehaviour() {
        return new Behavior() {

            @Override
            public void onComponentTag(Component component, ComponentTag tag) {
                tag.put("onclick", getOpenString());
            }
        };
    }

    /**
     * Return a Behavior which when applied to a Component will add an "onlick"
     * event handler that will close this Dialog.
     * @return
     */
    public Behavior getClickToCloseBehaviour() {
        return new Behavior() {

            @Override
            public void onComponentTag(Component component, ComponentTag tag) {
                tag.put("onclick", getCloseString());
            }
        };
    }

    /**
     * Returns the JavaScript required to open the dialog in the client browser.
     * Override to prefix or postfix with your own javascript code.
     * @return
     */
    protected String getOpenString() {
        if (!isEnabled()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append("jQuery('#").append(getMarkupId()).append("_ovl').show();");
        if (isSupportIE6()) {
            result.append("jQuery('#").append(getMarkupId()).append("_ovl').bgiframe();");
        }
        result.append("jQuery('#")
              .append(getMarkupId())
              .append("_ovl').height(jQuery(document).height()); jQuery('#")
              .append(getMarkupId())
              .append("').center(); jQuery('#")
              .append(getMarkupId())
              .append("').show();");

        return result.toString();
    }

    /**
     * Returns the JavaScript required to close the dialog in the client browser.
     * Override to prefix or postfix with your own javascript code.
     * @return
     */
    protected String getCloseString() {
        StringBuilder result = new StringBuilder();
        result.append("jQuery('#").append(getMarkupId()).append("').hide();");
        result.append("jQuery('#").append(getMarkupId()).append("_ovl').hide();");
        return result.toString();
    }

    /**
     * Override to enable BGI frame IE6 support.
     * @return
     */
    public boolean isSupportIE6() {
        return false;
    }

    /**
     * True if clicking the background will close the dialog.
     * @return
     */
    public boolean isClickBkgToClose() {
        return clickBkgToClose;
    }

    /**
     * Set whether the dialog should close when the user clicks the background (default: false);
     * @param clickBkgToClose
     */
    public void setClickBkgToClose(boolean clickBkgToClose) {
        this.clickBkgToClose = clickBkgToClose;
    }

    public IPrivilege getRenderPrivilege() {
        return IPrivilege.NULL;
    }

    public IPrivilege getEnablePrivilege() {
        return IPrivilege.NULL;
    }
    
}
