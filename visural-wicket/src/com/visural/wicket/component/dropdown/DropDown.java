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
package com.visural.wicket.component.dropdown;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import com.jquery.JQueryBGIFrameResourceReference;
import com.visural.common.StringUtil;
import com.visural.javascript.StringBufferResourceReference;
import com.visural.wicket.security.IPrivilege;
import com.visural.wicket.security.ISecureEnableInstance;
import com.visural.wicket.security.ISecureRenderInstance;

/**
 * DropDown is a form component which implements a rich-featured drop down (combo) box. *Requires JQuery*
 *
 * Apply to an `<input type="text"/>` component.
 *
 * The features of this dropdown in comparison to the HTML standard `select` control are:
 *
 *   * Can type into drop down box and the box will automatically appear and filter to input text
 *   * Can choose to filter / show all results
 *   * If multiple drop downs are rendered (say in a list) then they will only render the option values once into the markup, saving bandwidth.
 *   * Allows non-list values to be entered (i.e. choose from list or type your own)
 *   * Allows "must choose from list" mode - i.e. must choose a list value
 *
 * Include in page e.g. - `<input type="text" wicket:id="myDropDown"/>`
 *
 * When the box is working in `"requireListValue = true"` mode, then it the model
 * will expect to store the object of the same type as that stored in the lookup list.
 *
 * When the box is working in `"requireListValue = false"` mode, then it the model
 * will expect to store a String, regardless of the type of object which makes
 * up the lookup list.
 *
 * Limitations:
 *
 *   * onComponentTag() is final as it will have inconsistent results if used in
 *     the usual way. Try overriding `getValueAttributes()` instead.
 *
 * @version $Id: DropDown.java 266 2011-03-09 03:16:28Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class DropDown<T> extends TextField<T> implements Serializable, ISecureEnableInstance, ISecureRenderInstance {
    private static final long serialVersionUID = 1L;
    
    private final boolean outputPath;    
    private final DropDownDataSource source;
    private final boolean requireListValue;
    private String origMarkupId;
    private boolean enableFiltering = true;
    private boolean enableFilterToggle = true;
    private boolean showArrowIcon = true;
    private Integer overrideWidth = null;
    

    public DropDown(String id, DropDownDataSource source, boolean requireListValue) {
        this(id, null, source, requireListValue);
    }

    public DropDown(String id, IModel<T> model, DropDownDataSource source, boolean requireListValue) {
        this(id, model, null, source, requireListValue);
    }
    
    public DropDown(String id, IModel<T> model, Class<T> type, DropDownDataSource source, boolean requireListValue) {
        super(id, model, type);
        this.outputPath = Application.get().getDebugSettings().isOutputComponentPath();
        
        this.source = source;        
        this.requireListValue = requireListValue;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        origMarkupId = this.getMarkupId();
        this.setOutputMarkupId(true);
        if (requireListValue) {
            this.setMarkupId("visural_dropdown_id_"+origMarkupId);
        } else {
            this.setMarkupId("visural_dropdown_value_"+origMarkupId);
        }

        add(new Behavior() {

            @Override
            public void beforeRender(Component component) {
                component.getResponse().write("<span class=\"visural_dropdown_border\">");
            }

            @Override
            public void afterRender(Component component) {
                if (DropDown.this.requireListValue) {
                    String value = "";
                    if (StringUtil.isNotBlankStr(DropDown.this.getRawInput())) {
                        String lookup = DropDown.this.getRawInput();
                        T unsubmittedObject = convertValue(new String[]{lookup});
                        if (unsubmittedObject != null) {
                            value = DropDown.this.source.getDescriptionForValue(unsubmittedObject);
                        }
                    } else if (DropDown.this.getModelObject() != null) {
                        value = DropDown.this.source.getDescriptionForValue(DropDown.this.getModelObject());
                    }
                    component.getResponse().write(
                            String.format(
                                "<input %s type='text' %s value=\"%s\" id='visural_dropdown_value_%s' %s/>",
                                    DropDown.this.isEnabled() ? "" : "disabled ",
                                    getFlattenedValueAttributes(),
                                    StringUtil.htmlAttributeEscape(value),
                                    origMarkupId,
                                    outputPath ? "wicketpath=\""+getPageRelativePath()+"_vwdd\"" : ""));
                } else {
                    component.getResponse().write("<input type='hidden' id='visural_dropdown_id_"+origMarkupId+"'/>");
                }
                if (DropDown.this.isShowArrowIcon()) {
                    component.getResponse().write("<a class=\"visural_dropdown_arrow\" href=\"javascript:visural_dropdown_toggle('"+origMarkupId+"')\">" +
                            "<img class='visural_dropdown' alt='[v]' src='"+urlFor(getDropDownIconReference(), new PageParameters())+"'/>" +
                            "</a>");
                }
                component.getResponse().write("</span>");
                component.getResponse().write("<div id=\"visural_dropdown_"+origMarkupId+"\" class=\"visural_dropdown\" " +
                        "onMouseOver=\"visural_dropdown_mouseover('"+origMarkupId+"')\" " +
                        "onMouseOut=\"visural_dropdown_mouseout('"+origMarkupId+"')\"></div>");
            }
        });
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (autoAddToHeader()) {
        	response.render(JavaScriptReferenceHeaderItem.forReference(new PackageResourceReference(DropDown.class, "dropdown.js")));
        	response.render(JavaScriptReferenceHeaderItem.forReference(new StringBufferResourceReference()));
        	response.render(CssHeaderItem.forReference(getCSSHeaderContribution()));
            if (isSupportIE6()) {
            	response.render(JavaScriptReferenceHeaderItem.forReference(new JQueryBGIFrameResourceReference()));
            }
        }
        final String dsjs = DropDownDataSourceJSRender.getJS(source);
        response.render(OnDomReadyHeaderItem.forScript(dsjs));
        response.render(OnDomReadyHeaderItem.forScript(getInitJS()));
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
    protected void convertInput() {
        if (!requireListValue) {
            super.convertInput();
        } else {
            setConvertedInput(convertValue(getInputAsArray()));
        }
    }

    @Override
    public <C> IConverter<C> getConverter(Class<C> type) {
        if (requireListValue && source.getValues().size() > 0 && type.equals(source.getValues().get(0).getClass())) {
            return new IConverter() {
                public Object convertToObject(String listnum, Locale locale) {
                    T converted = null;
                    if (StringUtil.isNotBlankStr(listnum)) {
                        try {
                            converted = (T)source.getValues().get(Integer.parseInt(listnum));
                        } catch (Throwable t) {
                            throw new ConversionException("Could not convert '"+listnum+"' to list index.");
                        }
                    }
                    return converted;
                }

                public String convertToString(Object o, Locale locale) {
                    int idx = source.getValues().indexOf(o);
                    return (idx == -1 ? null : Integer.toString(idx));
                }
            };
        } else {
            return super.getConverter(type);
        }
    }

    @Override
    protected T convertValue(String[] value) throws ConversionException {
        if (!requireListValue) {
            return super.convertValue(value);
        } else {
            String listnum = (value != null && value.length > 0 && value[0] != null ? trim(value[0]) : null);
            T converted = null;
            if (StringUtil.isNotBlankStr(listnum)) {
                try {
                    converted = (T)this.source.getValues().get(Integer.parseInt(listnum));
                } catch (Throwable t) {
                    throw new ConversionException("Could not convert '"+listnum+"' to list index.");
                }
            }
            return converted;
        }
    }
   
    private String getInitJS() {
        StringBuilder sb = new StringBuilder();
        sb.append("visural_dropdowns['").append(origMarkupId).append("'] = new VisuralDropDown('")
                .append(origMarkupId)
                .append("', '")
                .append(this.source.getName())
                .append("', ")
                .append(!isRequireListValue())
                .append(", ")
                .append(isEnableFiltering() ? "true" : "false")
                .append(", ")
                .append(isEnableFilterToggle() ? "true" : "false")
                .append(", function() { ")
                .append(getOnValueChangeHandler())
                .append(" });\n");
        return sb.toString();
    }

    public final boolean isRequireListValue() {
        return requireListValue;
    }

    public boolean isEnableFiltering() {
        return enableFiltering;
    }

    public DropDown setEnableFiltering(boolean enableFiltering) {
        this.enableFiltering = enableFiltering;
        return this;
    }

    public boolean isEnableFilterToggle() {
        return enableFilterToggle;
    }

    public DropDown setEnableFilterToggle(boolean enableFilterToggle) {
        this.enableFilterToggle = enableFilterToggle;
        return this;
    }

    public boolean isShowArrowIcon() {
        return showArrowIcon;
    }

    public DropDown setShowArrowIcon(boolean showArrowIcon) {
        this.showArrowIcon = showArrowIcon;
        return this;
    }

    @Override
    protected final void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        // this is a cheat - when requiring a list value we make this object a
        // hidden field, as the ID attribute is the one we're actually interested in.
        if (isRequireListValue()) {
            tag.put("type", "hidden");
        }
        if (!requireListValue) {
            Map<String,String> valueAttributes = getValueAttributes();
            for (String attrib : valueAttributes.keySet()) {
                tag.put(attrib, valueAttributes.get(attrib));
            }
        }
    }

    private String getFlattenedValueAttributes() {
        Map<String,String> valueAttributes = getValueAttributes();
        StringBuffer result = new StringBuffer();
        for (String attrib : valueAttributes.keySet()) {
            result.append(" ");
            result.append(attrib);
            result.append("=\"");
            result.append(StringUtil.htmlAttributeEscape(valueAttributes.get(attrib)));
            result.append("\"");
        }
        return result.toString();
    }

    /**
     * Set the `size` attribute of the drop down entry field.
     * @param width
     */
    public DropDown setCharacterWidth(int width) {
        this.overrideWidth = Integer.valueOf(width);
        return this;
    }

    /**
     * Override to add new attributes to the `<input type="text" .../>` that
     * actually gets generated.
     *
     * Be sure to call super.getValueAttributes() as the basis for your result.
     *
     * @return map of attributes
     */
    protected Map<String,String> getValueAttributes() {
        Map<String,String> result = new HashMap<String,String>();
        result.put("onBlur", "visural_dropdown_blur('"+origMarkupId+"')");
        result.put("onKeyDown","return visural_dropdown_registerinput(event, '"+origMarkupId+"')");
        result.put("class", "visural_dropdown");
        result.put("onFocus", "visural_dropdown_focus('"+origMarkupId+"')");
        result.put("autocomplete", "off");
        if (overrideWidth != null) {
            result.put("size", overrideWidth.toString());
        }
        return result;
    }

    /**
     * Override to add a javascript call that is invoked whenever the dropdown's actual value changes.
     * @return
     */
    public String getOnValueChangeHandler() {
        return "";
    }

    /**
     * Override this method to enable IE6 support (bgiframe)
     * @return
     */
    public boolean isSupportIE6() {
        return false;
    }


    /**
     * Override to return a different dropdown.css (apply custom dropdown style).
     * @return
     */
    protected ResourceReference getCSSHeaderContribution() {
        return new PackageResourceReference(DropDown.class, "dropdown.css");
    }

    /**
     * Override to change the icon displayed as the "drop down" icon.
     * @return
     */
    protected ResourceReference getDropDownIconReference() {
        return new DropDownImageResourceRef();
    }

    public IPrivilege getRenderPrivilege() {
        return IPrivilege.NULL;
    }

    public IPrivilege getEnablePrivilege() {
        return IPrivilege.NULL;
    }

}
