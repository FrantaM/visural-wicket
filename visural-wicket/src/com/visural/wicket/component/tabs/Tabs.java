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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.resource.ResourceReference;

import com.visural.common.StringUtil;
import com.visural.wicket.security.IPrivilege;
import com.visural.wicket.security.ISecureEnableInstance;
import com.visural.wicket.security.ISecureRenderInstance;

/**
 *
 * @author Visural
 */
public class Tabs extends WebMarkupContainer implements ISecureEnableInstance, ISecureRenderInstance {
    private static final long serialVersionUID = 1L;
    
    private final List<Tab> tabs = new ArrayList<Tab>();
    private Comparator<Tab> comparator = null;

    public Tabs(String id) {
        super(id);
        setOutputMarkupId(true);
        add(AttributeModifier.replace("class", "tab_contents"));
        add(new Behavior() {

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
            	response.render(OnDomReadyHeaderItem.forScript("if (jQuery('#"+Tabs.this.getMarkupId()+"_tabs').length == 0) { " +
                        "var tempTabs = jQuery('#"+Tabs.this.getMarkupId()+"').clone();" +
                        "jQuery('"+tabsContainer().replace("'", "\\'")+"').insertAfter(jQuery('#"+Tabs.this.getMarkupId()+"'));" +
                        "jQuery('#"+Tabs.this.getMarkupId()+"').remove();" +
                        "tempTabs.insertAfter(jQuery('#"+Tabs.this.getMarkupId()+"_tabs'));" +
                        "} else { " +
                        "var selId = jQuery('#"+Tabs.this.getMarkupId()+"_tabs > .tab_selected').attr('id');" +
                        "jQuery('#"+Tabs.this.getMarkupId()+"_tabs').remove();" +
                        "jQuery('<div id=\""+getMarkupId()+"_tabs\">"+tabLinks().replace("'", "\\'")+"</div>').prependTo(jQuery('#"+getMarkupId()+"_border')); " +
                        "jQuery('#'+selId).trigger('click');" +
                        "}"));
            }

            private String tabsContainer() {
                StringBuilder pre = new StringBuilder("<div id=\""+getMarkupId()+"_border\" class=\"tabs\">");
                pre.append("<div id=\"").append(getMarkupId()).append("_tabs\">");
                pre.append(tabLinks());
                pre.append("</div></div>");
                return pre.toString();
            }

            private String tabLinks() {
                StringBuilder links = new StringBuilder();
                for (Tab t : tabs) {
                    if (t.isVisibleInHierarchy()) {
                        String clazz = getSelected().equals(t) ? "tab_selected" : "tab";
                        links.append("<a id=\"")
                            .append(t.getMarkupId())
                            .append("_link\" class=\"")
                            .append(clazz)
                            .append("\" href=\"javascript:void(0);\" onclick=\"")
                            .append(StringUtil.htmlAttributeEscape(getSelectTabString(t.getMarkupId()).toString()))
                            .append("\">")
                            .append(StringUtil.htmlEscape(t.getLabel()))
                            .append("</a>");
                    }
                }
                return links.toString();
            }           

        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        ResourceReference cssResource = getCSS();
        if (cssResource != null && autoAddToHeader()) {
        	response.render(CssHeaderItem.forReference(cssResource));
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

    private CharSequence getSelectTabString(String tabId) {
        StringBuilder sb = new StringBuilder();
        sb.append("jQuery('#")
          .append(getMarkupId())
          .append(" > .tabpage_selected').attr('class', 'tabpage'); jQuery('#")
          .append(tabId)
          .append("').attr('class','tabpage_selected');")
          .append("jQuery('#")
          .append(getMarkupId())
          .append("_tabs > .tab_selected').attr('class', 'tab'); jQuery('#")
          .append(tabId)
          .append("_link').attr('class','tab_selected');");;
        return sb;
    }

    public Tabs(String id, Tab... tabs) {
        this(id);
        for (Tab t : tabs) {
            add(t);
        }
    }

    public Tab add(Tab t) {
        super.add(t);
        tabs.add(t);
        return t;
    }

    public Comparator<Tab> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<Tab> comparator) {
        this.comparator = comparator;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (comparator != null) {
            Collections.sort(tabs, comparator);
        }
    }

    private int selectedIndex = 0;

    public final Tab getSelected() {
        // TODO - this will probably change
        return tabs.get(selectedIndex);
    }

    public void setSelectedTabIndex(int idx) {
        selectedIndex = idx;
    }

    protected ResourceReference getCSS() {
        return new TabsCSS();
    }

    public IPrivilege getRenderPrivilege() {
        return IPrivilege.NULL;
    }

    public IPrivilege getEnablePrivilege() {
        return IPrivilege.NULL;
    }

}
