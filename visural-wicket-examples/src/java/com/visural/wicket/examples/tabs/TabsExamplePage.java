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

package com.visural.wicket.examples.tabs;

import com.visural.wicket.aturl.At;
import com.visural.wicket.component.tabs.Tab;
import com.visural.wicket.component.tabs.Tabs;
import com.visural.wicket.examples.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

/**
 * @version $Id: DropDownExamplePage.java 94 2010-02-20 09:21:18Z tibes80@gmail.com $
 * @author Richard Nichols
 */
@At(url="/tabs")
public class TabsExamplePage extends BasePage {

    public TabsExamplePage() {
        final Tabs tabs;
        add(tabs = new Tabs("tabs"));
        tabs.setOutputMarkupId(true);
        Tab tab1 = new Tab("tab1", "Tab #1");
        Tab tab2 = new Tab("tab2", "Tab #2");
        final Tab tab3 = new Tab("tab3", "Tab #3");
        tabs.add(tab1); tabs.add(tab2); tabs.add(tab3);
        add(new AjaxLink("showHide") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(tabs);
                tab3.setVisible(!tab3.isVisible());
            }

        });
    }

    @Override
    public String getPageTitle() {
        return "Client-side Tabs (Javascript)";
    }
}
