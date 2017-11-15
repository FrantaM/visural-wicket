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
package com.visural.wicket.component.viewmode;

import com.visural.wicket.security.IPrivilege;
import com.visural.wicket.security.ISecureEnableInstance;
import com.visural.wicket.security.ISecureRenderInstance;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IComponentInheritedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;
import org.apache.wicket.model.Model;

/**
 * A wrapper for a FormComponent which replaces the component with a Label
 * when the wrapped control is in "view mode".
 *
 * Apply to a containing DOM element, e.g. `<span></span>` or `<div></div>`
 *
 * The component you provide to the constructor must be given as ID matching
 * `ViewOrEdit.COMP_ID`.
 *
 * The idea is to allow the rendering of web-like modern applications where
 * non-editable elements are represented in text form, using the same wicket
 * page / form implementation.
 *
 * What is "view mode" for a given component is determined by the method
 * `isViewMode()`. The default implementation of this method uses the component
 * and its form's `isEnabled()` state to determine view or edit.
 *
 * You may override this implementation and create any sort of complex logic you
 * wish for your specific use case.
 *
 * The `ViewOrEdit` container automatically creates a dynamic wrapping model so
 * that you can still apply `CompoundPropertyModels` to your form and have them
 * apply to form elements wrapped in a `ViewOrEdit` container. Just name your
 * `ViewOrEdit` element as per the property you wish to apply to the child
 * control.
 *
 * @version $Id: ViewOrEdit.java 256 2011-02-05 12:06:02Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class ViewOrEdit extends FormComponentPanel implements ISecureRenderInstance, ISecureEnableInstance {
    private static final long serialVersionUID = 1L;
    /**
     * This is the id of the component to be contained within the ViewOrEdit container.
     * Any component provided to the ViewOrEdit component should be identified as ViewOrEdit.COMP_ID
     */
    public final static String COMP_ID = "component";

    private final FormComponent component;
    private Label label;

    public ViewOrEdit(String id, FormComponent component) {
        this(id, component, null, null);
    }

    public ViewOrEdit(String id, FormComponent component, IModel labelModel) {
        this(id, component, labelModel, null);
    }

    public ViewOrEdit(String id, FormComponent component, ComponentModelToLabel componentModelToLabel) {
        this(id, component, null, componentModelToLabel);
    }

    protected ViewOrEdit(final String id, final FormComponent component, IModel labelModel, final ComponentModelToLabel componentModelToLabel) {
        super(id);
        this.component = component;

        if (labelModel == null && componentModelToLabel != null) {
            labelModel = new AbstractReadOnlyModel() {

                private final ComponentModelToLabel converter = componentModelToLabel;

                @Override
                public Object getObject() {
                    return converter.convertToLabel(component.getModelObject());
                }
            };
        }

        if (component == null || !component.getId().equals(COMP_ID)) {
            throw new IllegalArgumentException("The component provided to LabelOnViewOnly(...) must have the id \"" + COMP_ID + "\"");
        }

        Fragment f = resolveComponentFragment(labelModel);
        if (f == null) {
            throw new UnsupportedOperationException("No view mode fragment for component of type " + component.getClass().getName());
        }
        add(f);
    }

    /**
     * Provide support for using CompountPropertyModel or similar on form
     * and having that property model chain to the form component being wrapped
     * in a ViewOrEdit.
     *
     * @return
     */
    @Override
    protected IModel<?> initModel() {
        final IModel parentModel = super.initModel();
        if (parentModel != null && parentModel instanceof IWrapModel) {
            // we want to set this model to wrap the form component
            return new WrappedParentModel((IWrapModel)parentModel);
        }
        // we do this in case there is no parent model to avoid
        // "Attempt to set model object on null model of component" errors
        return new Model();
    }

    @Override
    public void convertInput() {
        // this forces a call to initModel()
        getDefaultModel();
    }

    /**
     * Determine whether the component is in view mode or not.
     *
     * By default, the implementation will assume view mode when this component
     * is not enabled or the form component is not enabled or the form itself
     * is not enabled, however this behaviour may be overriden as req'd.
     *
     * @return
     */
    public boolean isViewMode() {
        return !this.isEnabled() || !component.isEnabled() || !component.getForm().isEnabled();
    }

    @Override
    protected void onBeforeRender() {
        // this forces a call to initModel()
        getDefaultModel();
        // now proceed as normal
        super.onBeforeRender();
        boolean isView = this.isViewMode();
        label.setVisible(isView);
        component.setVisible(!isView);
    }

    private Fragment resolveComponentFragment(IModel labelModel) {
        if (labelModel == null) {
            // TODO: rather than doing this, maybe lookup converter?
            labelModel = new IModel() {

                public Object getObject() {
                    return (component.getModelObject() == null ? null : component.getModelObject().toString());
                }

                public void setObject(Object arg0) {
                }

                public void detach() {
                }
            };
        }
        label = new Label("viewLabel", labelModel);
        label.setEscapeModelStrings(isEscapeLabelModelStrings());
        if (TextField.class.isAssignableFrom(component.getClass())) {
            Fragment f = new Fragment("controlPair", "textfield", this);
            f.add(label);
            f.add(component);
            return f;
        }
        if (CheckBox.class.isAssignableFrom(component.getClass())) {
            Fragment f = new Fragment("controlPair", "checkbox", this);
            f.add(label);
            f.add(component);
            return f;
        }
        if (TextArea.class.isAssignableFrom(component.getClass())) {
            Fragment f = new Fragment("controlPair", "textarea", this);
            f.add(label);
            f.add(component);
            return f;
        }
        if (AbstractChoice.class.isAssignableFrom(component.getClass())) {
            Fragment f = new Fragment("controlPair", "choice", this);
            f.add(label);
            f.add(component);
            return f;
        }
        return null;
    }

    public FormComponent getComponent() {
        return component;
    }

    public boolean isEscapeLabelModelStrings() {
        return false;
    }

    class WrappedParentModel implements IComponentInheritedModel {
        private final IWrapModel realParent;

        public WrappedParentModel(IWrapModel realParent) {
            this.realParent = realParent;
        }

        public IWrapModel wrapOnInheritance(Component arg0) {
            if (arg0 == ViewOrEdit.this.component) {
                return realParent;
            } else {
                return null;
            }
        }

        public Object getObject() {
            return null;
        }

        public void setObject(Object arg0) {
        }

        public void detach() {
        }
    }

    public IPrivilege getRenderPrivilege() {
        return IPrivilege.NULL;
    }

    public IPrivilege getEnablePrivilege() {
        return IPrivilege.NULL;
    }

}
