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
package com.visural.wicket.component.submitters.impl;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * @version $Id: ModalHeaderContributor.java 261 2011-03-08 20:53:16Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class ModalHeaderContributor extends Behavior {

    private static final long serialVersionUID = 1L;

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.renderOnDomReadyJavaScript("jQuery(document.body).append('<div id=\"visuraloverlay_high\"></div><div id=\"visuraloverlaycontent_high\"></div>');");
    }
}
