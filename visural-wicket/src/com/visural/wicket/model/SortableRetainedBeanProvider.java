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

import java.util.Iterator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

/**
 * TODO: unfinished code
 *
 * An implementation of `SortableDataProvider` which utilites the {@link RetainedBeanModel}
 * to provide a list of java beans, which is sortable by property.
 *
 * @version $Id: SortableRetainedBeanProvider.java 256 2011-02-05 12:06:02Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class SortableRetainedBeanProvider<T,S> extends SortableDataProvider<T,S> {

    // TODO: implement.
    public SortableRetainedBeanProvider() {
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long size() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IModel<T> model(T arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
