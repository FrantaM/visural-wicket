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
package com.visural.wicket.behavior.inputhint;

import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.visural.common.StringUtil;

/**
 * Provides "hint text" inside `<input ...>` components similar to HTML5's
 * placeholder feature.
 * 
 * @version $Id$
 * @author Richard Nichols
 */
public class InputHintBehavior extends Behavior {

    private static final long serialVersionUID = 1L;

    private Set<Component> bound = new HashSet<Component>();
    private final String hintStyle;
    private final String entryStyle;
    private final Form form;
    private final String hintText;

    public InputHintBehavior(Form form, String hintText, String hintStyle) {
        this.hintStyle = hintStyle;
        this.entryStyle = "";
        this.form = form;
        this.hintText = hintText;
    }

    public InputHintBehavior(Form form, String hintText, String hintStyle, String entryStyle) {
        this.hintStyle = hintStyle;
        this.entryStyle = entryStyle;
        this.form = form;
        this.hintText = hintText;
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
    public void renderHead(Component component, IHeaderResponse response) {
        if (autoAddToHeader()) {
        	response.render(JavaScriptReferenceHeaderItem.forReference(new PackageResourceReference(InputHintBehavior.class, "visural-inputhint.js")));
        }
        response.render(JavaScriptReferenceHeaderItem.forScript(getInitJS(), null));
        response.render(OnDomReadyHeaderItem.forScript(getDomJS()));
    }

    private String getInitJS() {
        StringBuilder js = new StringBuilder();
        for (Component com : bound) {
            js.append("visural_inputHints['visural_ih_")
                .append(com.getMarkupId())
                .append("'] = new VisuralInputHint('")
                .append(com.getMarkupId())
                .append("','")
                .append(hintText.replace("'", "\\'"))
                .append("','")
                .append(hintStyle.replace("'", "\\'"))
                .append("', '")
                .append(entryStyle.replace("'", "\\'"))
                .append("');");
        }
        return js.toString();
    }

    private String getDomJS() {
        StringBuilder js = new StringBuilder();
        for (Component com : bound) {
            js.append("visural_inputHints['visural_ih_").append(com.getMarkupId()).append("'].handleBlur();");
        }
        return js.toString();
    }

    @Override
    public void bind(Component component) {
        bound.add(component);
        component.setOutputMarkupId(true);
        form.add(new InputHintFormBehavior(component.getMarkupId()));
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        String focus = tag.getAttribute("onfocus");
        String blur = tag.getAttribute("onblur");
        tag.put("onfocus", "visural_inputHints['visural_ih_"+component.getMarkupId()+"'].handleFocus();"+(StringUtil.isNotBlankStr(focus) ? focus : ""));
        tag.put("onblur", "visural_inputHints['visural_ih_"+component.getMarkupId()+"'].handleBlur();"+(StringUtil.isNotBlankStr(blur) ? blur : ""));
    }


}
