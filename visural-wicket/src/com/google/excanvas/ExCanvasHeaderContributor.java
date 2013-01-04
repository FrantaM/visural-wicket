/*
 *  Copyright 2010 Richard.
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
package com.google.excanvas;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.StringHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 * @author Richard
 */
public class ExCanvasHeaderContributor implements IHeaderContributor {

    private static final long serialVersionUID = 1L;

    public void renderHead(IHeaderResponse ihr) {
    	ihr.render(StringHeaderItem.forString("<!--[if IE]><script src=\"" + RequestCycle.get().urlFor(new ExCanvasJSRef(), new PageParameters()) + "\"></script><![endif]-->"));
    }
}
