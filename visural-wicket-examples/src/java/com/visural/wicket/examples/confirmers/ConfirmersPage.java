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
package com.visural.wicket.examples.confirmers;

import com.visural.wicket.aturl.At;
import com.visural.wicket.component.confirmer.ConfirmerAjaxLink;
import com.visural.wicket.component.confirmer.ConfirmerAjaxSubmitLink;
import com.visural.wicket.component.confirmer.ConfirmerSubmitLink;
import com.visural.wicket.examples.BasePage;
import java.io.Serializable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;

/**
 * @version $Id: SubmittersPage.java 119 2010-02-23 12:03:28Z tibes80@gmail.com $
 * @author Richard Nichols
 */
@At(url="/confirmers")
public class ConfirmersPage extends BasePage {

    public ConfirmersPage() {
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

            add(new ConfirmerAjaxSubmitLink("refreshingAjax") {
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

            add(new ConfirmerAjaxLink("random") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    xm.setNum((int)(Math.random()*200d-100d));
                    ym.setNum((int)(Math.random()*200d-100d));
                    answer = null;
                    target.add(x);
                    target.add(y);
                    target.add(answerText);
                }
            });
        }

        @Override
        protected void onSubmit() {
            calculate();
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
        return "Confirmers";
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
