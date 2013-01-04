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
package com.visural.wicket.behavior.jsr303;

import java.lang.annotation.Annotation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.AbstractPropertyModel;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.RangeValidator;
import org.apache.wicket.validation.validator.StringValidator;

import com.sun.org.apache.xml.internal.utils.StringVector;

/**
 * Looks at the bound component(s) models to see if they are any type of property model.
 * If so then inspect the bound property model field to see if it has JSR303 validation
 * annotations, and maps those to appropriate Wicket validators.
 * 
 * @version $Id$
 * @author Richard Nichols
 */
public class JSR303AnnotatedPropertyModelBehavior extends Behavior {

    private static final long serialVersionUID = 1L;

    @Override
    public void bind(Component component) {
        if (!addValidators(component)) {
            throw new WicketRuntimeException("Attempt to bind JSR303AnnotatedPropertyModelBehavior to component " + component.getId() +
                    ", but no model is (yet) available. Consider using JSR303FormVisitor in Form.onBeforeRender().");
        }
    }

    private boolean addValidators(Component component) {
        if (FormComponent.class.isAssignableFrom(component.getClass())) {
            FormComponent fc = (FormComponent) component;
            Object model = component.getDefaultModel();
            if (model != null) {
                if (AbstractPropertyModel.class.isAssignableFrom(model.getClass())) {
                    AbstractPropertyModel apm = (AbstractPropertyModel) model;
                    if (apm.getPropertyField() != null) {
                        Annotation[] annots = apm.getPropertyField().getAnnotations();
                        Long min = null;
                        Long max = null;
                        for (Annotation a : annots) {
                            if (a.annotationType().getName().equals("javax.validation.constraints.NotNull")) {
                                IValidator v = newNotNullValidator();
                                if (v == null) {
                                    fc.setRequired(true);
                                } else {
                                    fc.add(v);
                                }
                            } else if (a.annotationType().getName().equals("javax.validation.constraints.Min")) {
                                min = ((Min) a).value();
                            } else if (a.annotationType().getName().equals("javax.validation.constraints.Max")) {
                                max = ((Max) a).value();
                            } else if (a.annotationType().getName().equals("javax.validation.constraints.Past")) {
                                fc.add(newPastValidator());
                            } else if (a.annotationType().getName().equals("javax.validation.constraints.Future")) {
                                fc.add(newFutureValidator());
                            } else if (a.annotationType().getName().equals("javax.validation.constraints.Pattern")) {
                                Pattern pattern = (Pattern) a;
                                fc.add(newPatternValidator(pattern.regexp()));
                            } else if (a.annotationType().getName().equals("javax.validation.constraints.Size")) {
                                Size size = (Size) a;
                                fc.add(newSizeValidator(size.min(), size.max()));
                            } else if (a.annotationType().getName().equals(getEmailAnnotationClassName())) {
                                fc.add(newEmailValidator());
                            }
                        }
                        if (max != null || min != null) {
                            fc.add(newMinMaxValidator(min, max));
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Unlike other override points, if this method returns null, then the component
     * itself will be marked as setRequired(true);
     * @param regexp
     * @return
     */
    protected IValidator newNotNullValidator() {
        return null;
    }

    protected IValidator newFutureValidator() {
        return new FutureValidator(null);
    }

    protected IValidator newPastValidator() {
        return new PastValidator(null);
    }

    protected IValidator newPatternValidator(String regexp) {
        return new PatternValidator(regexp);
    }

    protected IValidator newSizeValidator(int min, int max) {
        if (min == 0) {
            return StringValidator.maximumLength(max);
        } else if (max == 2147483647) {
            return StringValidator.maximumLength(min);
        } else {
            return StringValidator.lengthBetween(min, max);
        }
    }

    protected IValidator newEmailValidator() {
        return EmailAddressValidator.getInstance();
    }

    protected IValidator newMinMaxValidator(Long min, Long max) {
        if (min != null && max != null) {
            return new RangeValidator(min, max);
        } else if (min != null) {
            return StringValidator.minimumLength(min.intValue());
        } else if (max != null) {
            return StringValidator.maximumLength(max.intValue());
        }
        return null;
    }

    /**
     * By default returns "org.hibernate.validator.constraints.Email"
     * If you have a different email address validation annotation, then
     * return your's instead.
     */
    protected String getEmailAnnotationClassName() {
        return "org.hibernate.validator.constraints.Email";
    }
}
