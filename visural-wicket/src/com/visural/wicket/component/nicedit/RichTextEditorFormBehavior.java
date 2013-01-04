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

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;

/**
 * Ensures that the display {@link RichTextEditor} content are synchronised with the hidden form DOM components.
 *
 * @version $Id: RichTextEditorFormBehavior.java 261 2011-03-08 20:53:16Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class RichTextEditorFormBehavior extends Behavior {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);
        if (tag.getAttribute("onsubmit") != null) {
            tag.put("onsubmit", tag.getAttribute("onsubmit")+";"+getRTEUpdateString());
        } else {
            tag.put("onsubmit", getRTEUpdateString());
        }
    }

    private String getRTEUpdateString() {
        return "for (var i = 0; i < nicEditors.editors.length; i++) { nicEditors.editors[i].nicInstances[0].saveContent(); }; return true;";
    }
}
