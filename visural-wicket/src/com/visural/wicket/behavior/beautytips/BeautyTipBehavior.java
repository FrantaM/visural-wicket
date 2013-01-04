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
package com.visural.wicket.behavior.beautytips;

import com.google.excanvas.ExCanvasHeaderContributor;
import com.visural.common.StringUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;

/**
 * An attractive, javascript-based tooltip.
 *
 * Integrates "Beautytips" from http://www.lullabot.com/files/bt/bt-latest/DEMO/index.html
 *
 *   * Requires JQuery
 *   * For Internet Explorer support, requires "{@link ExCanvasHeaderContributor}", since IE lacks canvas support.
 * 
 * @author Richard Nichols
 */
public class BeautyTipBehavior extends Behavior {
    private static final long serialVersionUID = 1L;
    
    private final String tip;
    private final IModel tipModel;
    private final Set<Component> componentIds = new HashSet<Component>();

    public BeautyTipBehavior(String tip) {
        this.tip = tip;
        tipModel = null;
    }

    public BeautyTipBehavior(IModel tipModel) {
        this.tipModel = tipModel;
        tip = null;
    }

    /**
     * Override and return false to suppress static JavaScript and CSS contributions.
     * (May be desired if you are concatenating / compressing resources as part of build process)
     * @return
     */
    protected boolean autoAddToHeader() {
        return true;
    }
    
    private Map<String,String> options = new HashMap<String,String>();

    /**
     * Applies the given CSS class to the item with the tip attached, when the
     * tip is active.
     * 
     * @param activeClass
     * @return
     */
    public BeautyTipBehavior setItemHoverClass(String activeClass) {
        options.put("activeClass", wrap(activeClass));
        return this;
    }

    public BeautyTipBehavior setTipClass(String tipClass) {
        options.put("cssClass", wrap(tipClass));
        return this;
    }

    public BeautyTipBehavior setPositionPreference(TipPosition... positions) {
        return setPositionPreference(Arrays.asList(positions));
    }

    public BeautyTipBehavior setPositionPreference(List<TipPosition> positions) {
        options.put("positions", "['"+StringUtil.delimitObjectsToString("','", positions)+"']");
        return this;
    }

    /**
     * Pass a set of CSS styling to the tool tip, e.g. color: '#FFF', fontWeight: 'bold'
     * Must be JSON format, not CSS format (i.e. comma separate, quoted strings)
     * @param tipStyle
     * @return
     */
    public BeautyTipBehavior setTipStyle(String tipStyle) {
        options.put("cssStyles", "{"+tipStyle+"}");
        return this;
    }

    public BeautyTipBehavior setBackgroundColor(String fill) {
        options.put("fill", wrap(fill));
        return this;
    }

    public BeautyTipBehavior setSpikeLength(int spikeLength) {
        options.put("spikeLength", Integer.toString(spikeLength));
        return this;
    }

    public BeautyTipBehavior setSpikeWidth(int spikeGirth) {
        options.put("spikeGirth", Integer.toString(spikeGirth));
        return this;
    }

    public BeautyTipBehavior setCornerRadius(int cornerRadius) {
        options.put("cornerRadius", Integer.toString(cornerRadius));
        return this;
    }

    public BeautyTipBehavior setBorderWidth(int strokeWidth) {
        options.put("strokeWidth", Integer.toString(strokeWidth));
        return this;
    }

    public BeautyTipBehavior setBorderColor(String borderColor) {
        options.put("strokeStyle", wrap(borderColor));
        return this;
    }

    public BeautyTipBehavior setPadding(int padding) {
        options.put("padding", Integer.toString(padding));
        return this;
    }

    public BeautyTipBehavior setShrinkToFit(boolean shrink) {
        options.put("shrinkToFit", shrink ? "true" : "false");
        return this;
    }

    public BeautyTipBehavior setWidth(int width) {
        options.put("width", width+"");
        return this;
    }

    public BeautyTipBehavior setFadeInOut(boolean fade) {
        if (fade) {
            options.put("showTip", "function(box){ jQuery(box).fadeIn(500); }");
            options.put("hideTip", "function(box, callback) { jQuery(box).animate({opacity: 0}, 500, callback); }");
        } else {
            options.remove("showTip");
            options.remove("hideTip");
        }
        return this;
    }

    public BeautyTipBehavior setDropShadow(boolean shadow) {
        if (shadow) {
            options.put("shadow", "true");
            options.put("shadowOffsetX", "3");
            options.put("shadowOffsetY", "3");
            options.put("shadowBlur", "8");
            options.put("shadowColor", wrap("rgba(0,0,0,.9)"));
            options.put("shadowOverlap", "false");
        } else {
            options.remove("shadow");
            options.remove("shadowOffsetX");
            options.remove("shadowOffsetY");
            options.remove("shadowBlur");
            options.remove("shadowColor");
            options.remove("shadowOverlap");
        }
        return this;
    }

    private String wrap(String content) {
        return "'"+content+"'";
    }

    @Override
    public void bind(Component component) {
        componentIds.add(component);
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        if (autoAddToHeader()) {
            new com.google.excanvas.ExCanvasHeaderContributor().renderHead(response);
            response.render(JavaScriptReferenceHeaderItem.forReference(new BeautyTipsJSRef()));
        }
        response.render(OnDomReadyHeaderItem.forScript(getJS()));
    }

    private String getJS() {
        StringBuilder sb = new StringBuilder();
        for (Component com : componentIds) {
            sb.append("jQuery(document.getElementById('").append(com.getMarkupId()).append("')).bt(").append(getOptionsJSON()).append(");");
        }
        return sb.toString();
    }

    private String getOptionsJSON() {
        if (options.size() == 0) {
            return "";
        }
        StringBuilder json = new StringBuilder("{");
        boolean comma = false;
        for (String option : options.keySet()) {
            if (comma) {
                json.append(",");
            }
            json.append(option).append(":").append(options.get(option));
            comma = true;
        }
        json.append("}");
        return json.toString();
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        if (tipModel == null) {
            tag.put("title", tip);
        } else {
            tag.put("title", (tipModel.getObject() == null ? "" : tipModel.getObject().toString()));
        }
    }
}
