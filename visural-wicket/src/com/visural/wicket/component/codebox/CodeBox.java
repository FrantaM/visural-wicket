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
package com.visural.wicket.component.codebox;

import java.io.Serializable;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.prettify.ExtraJSResourceReference;
import com.google.prettify.PrettifyCSSResourceReference;
import com.google.prettify.PrettifyJSResourceReference;
import com.visural.wicket.security.IPrivilege;
import com.visural.wicket.security.ISecureEnableInstance;
import com.visural.wicket.security.ISecureRenderInstance;

/**
 * A code block which does syntax highlighting of the code contents.
 *
 * Apply to `<pre></pre>` or `<code></code>` DOM element.
 *
 * Wraps the Prettify library by Google - http://code.google.com/p/google-code-prettify/
 *
 * It is not necessary to specify a language, prettify will guess the language
 * based on content, however an override is available should it be required.
 *
 * Apply to a `<pre>` or `<code>` block.
 *
 * @version $Id: CodeBox.java 261 2011-03-08 20:53:16Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class CodeBox extends WebComponent implements Serializable, ISecureEnableInstance, ISecureRenderInstance {

    private static final long serialVersionUID = 1L;
    private boolean displayLineNumbers = false;
    private CodeBoxLanguage languageOverride = null;

    /**
     * Create a Codebox with static content with the given `id`.
     * @param id
     */
    public CodeBox(String id) {
        super(id);
    }

    /**
     * Create a Codebox with the provided code content and the given `id`.
     * @param id
     * @param code source code to display
     */
    public CodeBox(final String id, String code) {
        this(id, new Model(code));
    }

    /**
     * Create a codebox with source code provided by an `IModel` and the given `id`.
     * @param id
     * @param model a model that will provide the source code to display
     */
    public CodeBox(final String id, IModel model) {
        super(id, model);
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
    public void renderHead(IHeaderResponse response) {
        if (autoAddToHeader()) {
        	response.render(CssHeaderItem.forReference(new PrettifyCSSResourceReference()));
        	response.render(JavaScriptReferenceHeaderItem.forReference(new PrettifyJSResourceReference()));
        }
        if (getLanguageOverride() != null && getLanguageOverride().getExtraJSfile() != null) {
        	response.render(JavaScriptReferenceHeaderItem.forReference(new ExtraJSResourceReference(getLanguageOverride())));
        }
        response.render(OnDomReadyHeaderItem.forScript("prettyPrint()"));
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        // check applied to code/pre (need to bring some code in from parent as can apply to either)
        if (!tag.getName().equalsIgnoreCase("pre") && !tag.getName().equalsIgnoreCase("code")) {
            findMarkupStream().throwMarkupException(
                    "Component " + getId() + " must be applied to a tag of type 'code' or 'pre', not " + tag.toUserDebugString());
        }
        // change display class
        if (getLanguageOverride() == null) {
            tag.put("class", "prettyprint");
        } else {
            tag.put("class", "prettyprint " + getLanguageOverride().getCSSClass());
        }
    }

    @Override
    public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
        String code = this.getDefaultModelObjectAsString();
        if (code != null) {
            if (isDisplayLineNumbers()) {
                code = formatLineNumbers(code);
            }
            replaceComponentTagBody(markupStream, openTag, code);
        } else {
            super.onComponentTagBody(markupStream, openTag);
        }
    }

    private String formatLineNumbers(String code) {
        StringBuilder codeWithLines = new StringBuilder(code.length() * 2);
        String[] lines = code.split("\n");
        int numPlaces = Integer.toString(lines.length).length();
        int lineNo = 1;
        for (String line : lines) {
            codeWithLines.append("<span class=\"nocode\">");
            codeWithLines.append(rightJustifyAndPad(lineNo++, numPlaces));
            codeWithLines.append(":</span> ");
            codeWithLines.append(line);
            codeWithLines.append('\n');
        }
        code = codeWithLines.toString();
        return code;
    }

    public boolean isDisplayLineNumbers() {
        return displayLineNumbers;
    }

    /**
     * Toggle the display of line numbers in the left gutter.
     * 
     * @param displayLineNumbers
     * @return
     */
    public CodeBox setDisplayLineNumbers(boolean displayLineNumbers) {
        this.displayLineNumbers = displayLineNumbers;
        return this;
    }

    public CodeBoxLanguage getLanguageOverride() {
        return languageOverride;
    }

    /**
     * Override the language used for syntax highligting.
     * 
     * @param languageOverride
     * @return
     */
    public CodeBox setLanguageOverride(CodeBoxLanguage languageOverride) {
        this.languageOverride = languageOverride;
        return this;
    }

    private String rightJustifyAndPad(int lineNo, int places) {
        StringBuilder result = new StringBuilder(places);
        result.append(lineNo);
        while (result.length() < places) {
            result.insert(0, ' ');
        }
        return result.toString();
    }

    public IPrivilege getRenderPrivilege() {
        return IPrivilege.NULL;
    }

    public IPrivilege getEnablePrivilege() {
        return IPrivilege.NULL;
    }

}
