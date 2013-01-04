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

import com.jquery.JQueryResourceReference;
import com.jquery.JQueryResourceReference.Version;
import com.visural.common.StringUtil;
import com.visural.wicket.component.fancybox.Fancybox;
import com.visural.wicket.examples.beautytips.TipExamplePage;
import com.visural.wicket.examples.codebox.CodeboxExamplePage;
import com.visural.wicket.examples.confirmers.ConfirmersPage;
import com.visural.wicket.examples.dateinput.DateInputExamplePage;
import com.visural.wicket.examples.dialog.DialogExamplePage;
import com.visural.wicket.examples.dropdown.DropDownExamplePage;
import com.visural.wicket.examples.fancybox.FancyBoxExamplePage;
import com.visural.wicket.examples.inputhint.InputHintExamplePage;
import com.visural.wicket.examples.jsr303.Jsr303ExamplePage;
import com.visural.wicket.examples.lesscss.LessCSSExamplePage;
import com.visural.wicket.examples.nicedit.RichTextEditorExamplePage;
import com.visural.wicket.examples.submitters.SubmittersPage;
import com.visural.wicket.examples.tabs.TabsExamplePage;
import com.visural.wicket.examples.vieworedit.ViewOrEditPage;
import com.visural.wicket.util.PageParamFactory;
import java.io.File;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

/** 
 *
 * @author Richard Nichols
 * @version 
 */
public abstract class BasePage extends WebPage {

    public static final boolean IS_PROD_MODE = new File("/tmp").exists(); // generally run on a linux host for net deploy

    public BasePage() {
    	String url = urlFor(new JQueryResourceReference(Version.V1_6_3), new PageParameters()).toString();
        add(new WebMarkupContainer("jquery").add(AttributeModifier.replace("src", Model.of(url))));
        add(new Label("pageTitle", new PageTitleModel(false)));
        add(new Label("pageTitleHeader", new PageTitleModel(true)).setEscapeModelStrings(false));
        add(new BookmarkablePageLink("tabsLink", TabsExamplePage.class));
        add(new BookmarkablePageLink("dropdown", DropDownExamplePage.class));
        add(new BookmarkablePageLink("fancybox", FancyBoxExamplePage.class));
        add(new BookmarkablePageLink("inputHintLink", InputHintExamplePage.class));
        add(new BookmarkablePageLink("nicedit", RichTextEditorExamplePage.class));
        add(new BookmarkablePageLink("codebox", CodeboxExamplePage.class));
        add(new BookmarkablePageLink("submitters", SubmittersPage.class));
        add(new BookmarkablePageLink("confirmers", ConfirmersPage.class));
        add(new BookmarkablePageLink("vieworedit", ViewOrEditPage.class));
        add(new BookmarkablePageLink("dateInput", DateInputExamplePage.class));
        add(new BookmarkablePageLink("dialogBut", DialogExamplePage.class));
        add(new BookmarkablePageLink("tooltips", TipExamplePage.class));
        add(new BookmarkablePageLink("jsr303", Jsr303ExamplePage.class));
        add(new BookmarkablePageLink("lesscss", LessCSSExamplePage.class));

        add(new WebMarkupContainer("analytics").setVisible(IS_PROD_MODE));

        add(new Fancybox("viewSource",
                urlFor(ViewSourcePage.class, PageParamFactory.get(ViewSourcePage.PARAM_RESOURCE, getSourcePath())) + "").setHeight(768).setWidth(1024));
        add(new Fancybox("viewMarkup",
                urlFor(ViewSourcePage.class, PageParamFactory.get(ViewSourcePage.PARAM_RESOURCE, getMarkupPath())) + "").setHeight(768).setWidth(1024));

        add(new Label("title", new AbstractReadOnlyModel() {
            @Override
            public Object getObject() {
                return getPageTitle();
            }
        }));
    }

    @Override
    public void renderHead(org.apache.wicket.markup.head.IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(BasePage.class, "style.css")));
    }

    private String getSourcePath() {
        String path = this.getClass().getName();
        path = path.replace('.', '/');
        return "/" + path + ".java";
    }

    private String getMarkupPath() {
        String path = this.getClass().getName();
        path = path.replace('.', '/');
        return "/" + path + ".html";
    }

    public abstract String getPageTitle();

    /*
     * Need to lazy-init the title text as it will not be available at time of
     * construction in most cases (dynamic).
     */
    class PageTitleModel implements IModel {
        private final boolean displayImage;
        public PageTitleModel(boolean displayImage) {
            this.displayImage = displayImage;
        }

        private String getTitle() {
            if (StringUtil.isBlankStr(getPageTitle())) {
                return "Visural Wicket Examples";
            } else {
            return "Visural Wicket Examples - "+getPageTitle();
        }
        }

        public Object getObject() {
            if (displayImage) {
                return "<a href=\"http://code.google.com/p/visural-wicket/\"><img src=\""+urlFor(new LogoRef(), new PageParameters())+"\"/></a> "+getTitle();
            } else {
                return getTitle();
            }
        }
        public void setObject(Object object) {
        }
        public void detach() {
        }
    }

    /*
     * old ga tracking code

<!--            <script type="text/javascript">
            var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
            document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
            </script>
            <script type="text/javascript">
            try {
            var pageTracker = _gat._getTracker("UA-581879-10");
            pageTracker._trackPageview();
            } catch(err) {}</script>-->
     */
}
