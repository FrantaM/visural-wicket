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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.DateValidator;

/**
 * Validates that a Date must occur in the future
 * @version $Id$
 * @author Richard Nichols
 */
public class FutureValidator extends DateValidator {

    private static final long serialVersionUID = 1L;
    private final String format;

    public FutureValidator(String format) {
        this.format = format;
    }

//    @Override
//    protected Map<String, Object> variablesMap(IValidatable<Date> validatable) {
//        final Map<String, Object> map = super.variablesMap(validatable);
//        if (format == null) {
//            map.put("inputdate", validatable.getValue());
//        } else {
//            SimpleDateFormat sdf = new SimpleDateFormat(format);
//            map.put("inputdate", sdf.format(validatable.getValue()));
//        }
//        return map;
//    }

//    @Override
//    protected String resourceKey() {
//        return "DateValidator.future";
//    }

    @Override
    public void validate(IValidatable<Date> validatable) {
    	Date value = validatable.getValue();
        if (value.before(new Date())) {
        	IValidationError error= new ValidationError("Date is not in the future");
			validatable.error(error);
        }
    }
}
