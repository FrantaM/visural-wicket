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
package com.visural.wicket.component.nicedit;

import com.nicedit.NiceditIconsRef;
import com.nicedit.NiceditJSRef;
import com.visural.wicket.security.IPrivilege;
import com.visural.wicket.security.ISecureEnableInstance;
import com.visural.wicket.security.ISecureRenderInstance;
import com.visural.wicket.util.RenderAsInlineBlockComponentBorder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Rich Text Editor component (i.e. WYSIWYG HTML editor).
 *
 * Apply to a `<textarea></textarea>` component.
 *
 * Note, that you must add {@link RichTextEditorFormBehavior} as a behavior to
 * your enclosing Form in order for RichTextEditor models to be updated correctly.
 *
 * This is due to the way that Nicedit attachs it's onSubmit() behavior not
 * being compatible with wicket-based forms.
 *
 * @version $Id: RichTextEditor.java 265 2011-03-09 02:17:15Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class RichTextEditor<T> extends TextArea<T> implements ISecureEnableInstance, ISecureRenderInstance {
    private static final long serialVersionUID = 1L;
    
    public RichTextEditor(final String id) {
        super(id);
        init();
    }

    public RichTextEditor(final String id, final IModel<T> model) {
        super(id, model);
        init();
    }

    /**
     * Override and return false to suppress static JavaScript and CSS contributions.
     * (May be desired if you are concatenating / compressing resources as part of build process)
     * @return
     */
    protected boolean autoAddToHeader() {
        return true;
    }

    private void init() {
        this.setOutputMarkupId(true);
        add(new RenderAsInlineBlockComponentBorder());
        loadDefaultButtonConfig();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (autoAddToHeader()) {
        	HeaderItem hdr;
			response.render(JavaScriptReferenceHeaderItem.forReference(new NiceditIconsRef()));
        }
        response.render(JavaScriptContentHeaderItem.forScript(getInitJS(), "visural-richTextEditor-js-id"));
    }


    
    private String getInitJS() {
        return "new nicEditor("+getConfigString()+").panelInstance('"+this.getMarkupId()+"');";
    }

    private Map<String, Boolean> buttons = new LinkedHashMap<String, Boolean>();
    private Integer maxHeight = null;

    private void loadDefaultButtonConfig() {
        for (Button button : Button.values()) {
            buttons.put(button.name(), isButtonEnabled(button));
        }
    }

    /**
     * This method is called during initialisation to determine whether particular
     * Nicedit control buttons should be displayed or not.
     *
     * By default, all buttons except `xhtml`, `image` and `upload` are enabled.
     *
     * If you wish to disable more buttons, then override this method and return
     * the appropriate conditional values.
     *
     * @param button
     * @return
     */
    public boolean isButtonEnabled(Button button) {
        switch (button) {
            case xhtml: return false;
            case image: return false;
            case upload: return false;
            default: return true;
        }
    }

    private String getConfigString() {
        StringBuilder config = new StringBuilder("{");
        config.append("iconsPath : '").append(urlFor(new NiceditIconsRef(), new PageParameters())).append("'");
        
        config.append(",");

        config.append("buttonList : [");
        boolean addComma = false;
        for (Map.Entry<String,Boolean> button : buttons.entrySet()) {
            if (button.getValue()) {
                if (addComma) {
                    config.append(",");
                }
                config.append("'");
                config.append(button.getKey());;
                config.append("'");
                addComma = true;
            }
        }
        config.append("]");
        if (getMaxHeight() != null) {
            config.append(", maxHeight : ");
            config.append(getMaxHeight().toString());
        }
        config.append("}");
        return config.toString();
    }

    /**
     * Return the maximum height of the text area.
     * See http://nicedit.com/docs.php
     * @return
     */
    public Integer getMaxHeight() {
        return maxHeight;
    }

    /**
     * Change the maximum height for the text area
     * See http://nicedit.com/docs.php
     * @param maxHeight
     */
    public void setMaxHeight(Integer maxHeight) {
        this.maxHeight = maxHeight;
    }

    public IPrivilege getRenderPrivilege() {
        return IPrivilege.NULL;
    }

    public IPrivilege getEnablePrivilege() {
        return IPrivilege.NULL;
    }
    
}
