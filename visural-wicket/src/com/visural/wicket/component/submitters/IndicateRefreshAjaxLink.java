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

import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;

import com.visural.wicket.component.submitters.impl.SmallAjaxIndicatorRef;
import com.visural.wicket.security.IPrivilege;
import com.visural.wicket.security.ISecureEnableInstance;
import com.visural.wicket.security.ISecureRenderInstance;

/**
 * An AJAX `Link` which replaces the given elements with an indicator image, while the update occurs. *Requires JQuery*
 *
 * @version $Id: IndicateRefreshAjaxLink.java 261 2011-03-08 20:53:16Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public abstract class IndicateRefreshAjaxLink extends AjaxLink implements ISecureEnableInstance, ISecureRenderInstance {
    private static final long serialVersionUID = 1L;
    // TODO: modularise this with AjaxSubmitLink

    public static final int AJAX_SUBMIT_DELAY_WINDOW = 300;

    public IndicateRefreshAjaxLink(String id) {
        super(id);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("jQuery('<img />').attr('src', '" + urlFor(getRefreshIndicatorImageReference(), new PageParameters()) + "');"));
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
     * @return the collection of web markup containers to be replaced with an AJAX
     *         indicator. Note that you must setOutputMarkupId(true) on each of
     *         these elements.
     */
    protected abstract Collection<? extends Component> getIndicateRefreshContainers();

    /**
     * You may override this method to modify the delay threshold before the
     * indicator is displayed.
     *
     * @return the milliseconds to wait before displaying the AJAX indicator.
     */
    protected int getIndicatorDisplayThresholdMillis() {
        return AJAX_SUBMIT_DELAY_WINDOW;
    }

    /**
     * You may override the html that is inserted into the page to something
     * other than a simple AJAX indicator image.
     *
     * This method also would allow you to implement different indicators for
     * different page components should you need to.
     *
     * @return the html to be replaced for the given container component.
     */
    protected String getIndicatorHTML(Component container) {
        return "<img src=\""+urlFor(getRefreshIndicatorImageReference(), new PageParameters())+"\"/>";
    }

    /**
     * Override to modify the AJAX indicator image used.
     *
     * @return resource reference to AJAX indicator image.
     */
    protected ResourceReference getRefreshIndicatorImageReference() {
        return new SmallAjaxIndicatorRef();
    }

    private String getAjaxImageReplaceScript() {
        Collection<? extends Component> containers = getIndicateRefreshContainers();
        StringBuilder result = new StringBuilder();
        if (containers != null) {
            // TODO: refactor JS - fix namespacing across all components
            result.append("VISURAL").append(this.getMarkupId()).append("Complete = false; window.setTimeout(function() { if (!VISURAL").append(this.getMarkupId()).append("Complete) { ");
            for (Component container : containers) {
                result.append("jQuery('#").append(container.getMarkupId()).append("').hide();");
                result.append("jQuery('#").append(container.getMarkupId()).append("').after('<span class=\"visuralajaxind_").append(this.getId()).append("\">").append(getIndicatorHTML(container)).append("</span>');");
            }
            result.append("} }, ").append(getIndicatorDisplayThresholdMillis()).append(");");
        }
        return result.toString();
    }

    private String getAjaxImageUnreplaceScript() {
        Collection<? extends Component> containers = getIndicateRefreshContainers();
        StringBuilder result = new StringBuilder();
        if (containers != null) {
            // TODO: refactor JS - fix namespacing across all components
            result.append("VISURAL").append(IndicateRefreshAjaxLink.this.getMarkupId()).append("Complete = true;");
            for (Component container : containers) {
                result.append("jQuery('#").append(container.getMarkupId()).append("').show();");
                result.append("jQuery('.visuralajaxind_").append(this.getId()).append("').remove();");
            }
        }
        return result.toString();
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
    	super.updateAjaxAttributes(attributes);

    	attributes.getAjaxCallListeners().add(getAjaxCallDecorator());
    }

    protected IAjaxCallListener getAjaxCallDecorator() {
        return new AjaxCallListener() {

			@Override
			public CharSequence getBeforeHandler(Component component) {
				return null;
			}

			@Override
			public CharSequence getPrecondition(Component component) {
				return getAjaxImageReplaceScript();
			}

			@Override
			public CharSequence getBeforeSendHandler(Component component) {
				return null;
			}

			@Override
			public CharSequence getAfterHandler(Component component) {
				return null;
			}

			@Override
			public CharSequence getSuccessHandler(Component component) {
				return getAjaxImageUnreplaceScript();
			}

			@Override
			public CharSequence getFailureHandler(Component component) {
				return getAjaxImageUnreplaceScript();
			}

			@Override
			public CharSequence getCompleteHandler(Component component) {
				return null;
			}
        };
    }

    public IPrivilege getRenderPrivilege() {
        return IPrivilege.NULL;
    }

    public IPrivilege getEnablePrivilege() {
        return IPrivilege.NULL;
    }


}
