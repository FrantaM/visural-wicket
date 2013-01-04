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
package com.visural.wicket.examples;

import com.visural.wicket.aturl.AtAnnotation;
import com.visural.wicket.util.lesscss.LessCSSResourceStreamLocator;
import org.apache.wicket.protocol.http.WebApplication;

/** 
 *
 * @author Richard Nichols
 * @version 
 */
public class ExamplesApplication extends WebApplication {

    public ExamplesApplication() {
    }

    @Override
    protected void init() {
        super.init();
        try {
            AtAnnotation.mount(this, "com.visural.wicket.examples");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Failed mounting URLs.", ex);
        }
        getResourceSettings().setResourcePollFrequency(null);
        getResourceSettings().setResourceStreamLocator(new LessCSSResourceStreamLocator());
        getDebugSettings().setOutputComponentPath(true);
    }
//
//    @Override
//    protected ISessionStore newSessionStore() {
//        return new HttpSessionStore(this);
//    }

    public Class getHomePage() {
        return ExampleIndexPage.class;
    }
}
