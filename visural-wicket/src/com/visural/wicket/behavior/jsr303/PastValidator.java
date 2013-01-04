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
 * Validates that a date must occur in the past
 * @version $Id$
 * @author Richard Nichols
 */
public class PastValidator extends DateValidator {

    private static final long serialVersionUID = 1L;
    private final String format;

    public PastValidator(String format) {
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
//
//    @Override
//    protected String resourceKey() {
//        return "DateValidator.past";
//    }

    
    @Override
    public void validate(IValidatable<Date> validatable) {
    	Date value = validatable.getValue();
        if (value.after(new Date())) {
        	IValidationError error = new ValidationError("Dat is not in the past");
			validatable.error(error);
        }
    }
}
