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
package com.visural.wicket.model;

import com.visural.common.DateUtil;
import java.util.Date;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * A property column extension that allows you to override the format for a Date property.
 *
 * @version $Id: DatePropertyColumn.java 256 2011-02-05 12:06:02Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class DatePropertyColumn extends PropertyColumn {
    private static final long serialVersionUID = 1L;
    private final String dateFormat;

    public DatePropertyColumn(org.apache.wicket.model.IModel displayModel, java.lang.String propertyExpression, String dateFormat) {
        super(displayModel, propertyExpression);
        this.dateFormat = dateFormat;
    }

    public DatePropertyColumn(org.apache.wicket.model.IModel displayModel, java.lang.String sortProperty, java.lang.String propertyExpression, String dateFormat) {
        super(displayModel, sortProperty, propertyExpression);
        this.dateFormat = dateFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public IModel getDataModel(final IModel itemModel) {
        final IModel superModel = super.getDataModel(itemModel);
        if (superModel != null && superModel.getObject() != null && Date.class.isAssignableFrom(superModel.getObject().getClass())) {
            return new AbstractReadOnlyModel() {
                public Object getObject() {
                    return DateUtil.formatDate((Date)superModel.getObject(), dateFormat);
                }
            };
        } else {
            return superModel;
        }
    }
}
