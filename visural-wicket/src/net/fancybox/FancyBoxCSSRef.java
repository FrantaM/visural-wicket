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
package net.fancybox;

import org.apache.wicket.ResourceReference;

/**
 * @version $Id: FancyBoxCSSRef.java 217 2010-09-30 23:57:10Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class FancyBoxCSSRef extends ResourceReference {
    private static final long serialVersionUID = 1L;
    public FancyBoxCSSRef() {
        super(FancyBoxCSSRef.class, "jquery.fancybox-1.3.1.css");
    }
}
