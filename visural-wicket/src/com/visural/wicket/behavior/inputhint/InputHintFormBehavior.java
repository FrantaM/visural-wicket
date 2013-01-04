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

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Form;

/**
 * Ensures that the form does not post data from {@link InputHintBehavior} where
 * no data has been entered by a user.
 *
 * @version $Id: RichTextEditorFormBehavior.java 109 2010-02-23 01:47:12Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class InputHintFormBehavior extends Behavior {

    private static final long serialVersionUID = 1L;

    private final String componentMarkupId;

    public InputHintFormBehavior(String componentMarkupId) {
        this.componentMarkupId = componentMarkupId;
    }

    @Override
    public void bind(Component component) {
        if (!Form.class.isAssignableFrom(component.getClass())) {
            throw new WicketRuntimeException("This behavior must be applied to a Form, not a regular Component.");
        }
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);
        String onSub = null;
        if (tag.getAttribute("onsubmit") != null) {
            onSub = getIHJS()+";"+tag.getAttribute("onsubmit");
        } else {
            onSub = getIHJS();
        }
        if (!onSub.endsWith("return true;")) {
            onSub = onSub + "; return true;";
        }
        tag.put("onsubmit", onSub);
    }

    private String getIHJS() {
        return "if (jQuery('#"+componentMarkupId+"').val() === visural_inputHints['visural_ih_"+componentMarkupId+"'].hintText) { jQuery('#"+componentMarkupId+"').val(''); }";
    }
}
