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
package com.visural.wicket.aturl;

import java.util.Set;

import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import com.visural.common.ClassFinder;

/**
 * Used to mount {@link At} annotated WebPages at given urls, using either the
 * default coding strategy, or a specified type.
 *
 * Usage:
 *
 *     public class MyApp extends WebApplication {
 *         protected void init() {
 *             try {
 *                 AtAnnotation.mount(this, "com.mycom.myapp");
 *             } catch (ClassNotFoundException ex) {
 *                 throw new IllegalStateException("Failed mounting URLs.", ex);
 *             }
 *         }
 *         ...
 *     }
 * 
 * @version $Id: AtAnnotation.java 261 2011-03-08 20:53:16Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class AtAnnotation {

    /**
     * Search the class path for `@At` annotated classes to mount against a WebApplication.
     *
     * @param app WebApplication to mount against
     * @param packageBase base package to scan from, e.g. "com.mycompany.appname"
     * @throws ClassNotFoundException if a Class path scanning error occurs
     */
    public static void mount(WebApplication app, String packageBase) throws ClassNotFoundException {
        Set<Class> pages = findClasses(packageBase);
        for (Class page : pages) {
            mountPage(app, page);
        }
    }

    private static Set<Class> findClasses(String packageBase) throws ClassNotFoundException {
        ClassFinder finder = new ClassFinder(packageBase, true);
        finder.addClassAnnotationFilter(At.class);
        finder.addSuperClassFilter(WebPage.class);
        return finder.find();
    }

    private static void mountPage(WebApplication app, Class page) {
        At at = (At) page.getAnnotation(At.class);
        switch (at.type()) {
            case Standard: 
            case StateInURL: 
                {            
                    StringBuilder url = new StringBuilder(at.url());
                    for (String param : at.urlParameters()) {
                        url.append("/#{").append(param).append("}"); // all optional
                    }
                    app.mount(new MountedMapper(url.toString(), page));
                }
                break;
            case Indexed: 
            case IndexedStateInURL:
                {
                    StringBuilder url = new StringBuilder(at.url());
                    for (int n = 0; n < 10; n++) {
                        url.append("/#{").append(n).append("}");
                    }
                    app.mount(new MountedMapper(url.toString(), page));
                }
                break;
            case MountedMapper:
                {
                    app.mount(new MountedMapper(at.url(), page));
                }
                break;
        }

    }
}
