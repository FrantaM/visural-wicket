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
package com.visural.wicket.examples.submitters;

import com.visural.wicket.aturl.At;
import com.visural.wicket.component.submitters.IndicateModalSubmitLink;
import com.visural.wicket.component.submitters.IndicateRefreshAjaxLink;
import com.visural.wicket.component.submitters.IndicateRefreshAjaxSubmitLink;
import com.visural.wicket.examples.BasePage;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;

/**
 * @version $Id: SubmittersPage.java 256 2011-02-05 12:06:02Z tibes80@gmail.com $
 * @author Richard Nichols
 */
@At(url="/submitters")
public class SubmittersPage extends BasePage {

    public SubmittersPage() {
        add(new SubmitForm("calcForm"));
    }

    class SubmitForm extends Form {

        TextField<Integer> x;
        IntHolder xm = new IntHolder();
        TextField<Integer> y;
        IntHolder ym = new IntHolder();
        Label answerText;
        Integer answer = null;
        FeedbackPanel feedback;

        public SubmitForm(String id) {
            super(id);
            add((feedback = new FeedbackPanel("feedback")).setOutputMarkupId(true));
            add((x = new TextField<Integer>("numx", new PropertyModel(xm, "num"))).setRequired(true).setOutputMarkupId(true));
            add((y = new TextField<Integer>("numy", new PropertyModel(ym, "num"))).setRequired(true).setOutputMarkupId(true));
            add((answerText = new Label("answer", new AbstractReadOnlyModel() {
                @Override
                public Object getObject() {
                    return (answer == null ? "?" : answer+"");
                }
            })).setOutputMarkupId(true));

            add(new IndicateRefreshAjaxSubmitLink("refreshingAjax") {
                @Override
                protected Collection<? extends Component> getIndicateRefreshContainers() {
                    return Arrays.asList(answerText);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedback);
                }

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    target.add(answerText);
                    target.add(feedback);
                }
            });

            add(new IndicateRefreshAjaxLink("random") {
                @Override
                protected Collection<? extends Component> getIndicateRefreshContainers() {
                    return Arrays.asList(x, y, answerText);
                }

                @Override
                public void onClick(AjaxRequestTarget target) {
                    xm.setNum((int)(Math.random()*200d-100d));
                    ym.setNum((int)(Math.random()*200d-100d));
                    answer = null;
                    target.add(x);
                    target.add(y);
                    target.add(answerText);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {

                    }
                }
            });

            add(new IndicateModalSubmitLink("modal"));
        }

        @Override
        protected void onSubmit() {
            try {
                calculate();
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                
            }
        }

        private void calculate() {
            if (x.getModelObject() != null && y.getModelObject() != null) {
                answer = x.getModelObject() * y.getModelObject();
            } else {
                answer = null;
            }
        }


    }

    @Override
    public String getPageTitle() {
        return "Submitters and Links";
    }

    class IntHolder implements Serializable {
        Integer num = (int)(Math.random()*200d-100d);

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }
        
    }
}
