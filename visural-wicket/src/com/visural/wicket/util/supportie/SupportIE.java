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
package com.visural.wicket.util.supportie;

import java.util.Map;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * A collection of IE compatibility hacks.
 * 
 * @version $Id$
 * @author Richard Nichols
 */
public class SupportIE {

    /**
     * A PNG-transparency fix solution for IE6, which fixes all PNGs that are
     * selected by the given CSS selector(s).
     * 
     * @param urlForPNGRef
     * @param cssSelectors
     * @return
     */
    public static IHeaderContributor getFixPNGContributor(String urlForPNGRef, String[] cssSelectors) {
        StringBuffer js = new StringBuffer("<!--[if IE 6]><script src=\"");
        js.append(urlForPNGRef);
        js.append("\"></script><script type=\"text/javascript\">");
        for (String selector : cssSelectors) {
            js.append("DD_belatedPNG.fix('");
            js.append(selector);
            js.append("');");
        }
        js.append("</script><![endif]-->");
        final String hdr = js.toString();
        return new IHeaderContributor() {

            public void renderHead(IHeaderResponse arg0) {
                arg0.renderString(hdr);
            }
        };
    }

    /**
     * Add's rounded corners under IE using Javascript. Has no effect on other browsers.
     *
     * (Note that depending the rest of your page CSS, YMMV!)
     *
     * @param urlForRoundiesRef
     * @param selectorAndRounding
     * @return
     */
    public static IHeaderContributor getRoundiesContributor(String urlForRoundiesRef, Map<String,String> selectorAndRounding) {
        StringBuffer js = new StringBuffer("<!--[if IE]><script src=\"");
        js.append(urlForRoundiesRef);
        js.append("\"></script><script type=\"text/javascript\">");
        for (String selector : selectorAndRounding.keySet()) {
            js.append("DD_roundies.addRule('");
            js.append(selector);
            js.append("','");
            js.append(selectorAndRounding.get(selector));
            js.append("');");
        }
        js.append("</script><![endif]-->");
        final String hdr = js.toString();
        return new IHeaderContributor() {

            public void renderHead(IHeaderResponse arg0) {
                arg0.renderString(hdr);
            }
        };
    }
}
