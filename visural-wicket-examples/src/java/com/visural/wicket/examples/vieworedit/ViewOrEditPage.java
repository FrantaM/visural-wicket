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

package com.visural.wicket.examples.vieworedit;

import com.visural.wicket.aturl.At;
import com.visural.wicket.component.dropdown.DropDown;
import com.visural.wicket.component.dropdown.DropDownDataSource;
import com.visural.wicket.component.submitters.IndicateModalLink;
import com.visural.wicket.component.submitters.IndicateModalSubmitLink;
import com.visural.wicket.component.viewmode.ComponentModelToLabel;
import com.visural.wicket.component.viewmode.ViewOrEdit;
import com.visural.wicket.examples.BasePage;
import com.visural.wicket.examples.dropdown.Country;
import com.visural.wicket.examples.dropdown.CountryFactory;
import java.io.Serializable;
import java.util.List;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * @version $Id: ViewOrEditPage.java 256 2011-02-05 12:06:02Z tibes80@gmail.com $
 * @author Richard Nichols
 */
@At(url="/view-or-edit")
public class ViewOrEditPage extends BasePage {

    private List<Country> countries = CountryFactory.getCountries();
    private DropDownDataSource countriesDS;
    private ViewOrEditForm form;

    public ViewOrEditPage() {
        buildDataSource();
        add((form = new ViewOrEditForm("viewOrEditForm")));
        add(new IndicateModalLink("toggle") {
            @Override
            public void onClick() {
                form.setEnabled(!form.isEnabled());
            }
            @Override
            public boolean isVisible() {
                return !form.formLink1.isVisible();
            }
        });
        add(new IndicateModalLink("toggleAddr") {
            @Override
            public void onClick() {
                form.enableAddress = !form.enableAddress;
            }
            @Override
            public boolean isVisible() {
                return !form.formLink2.isVisible();
            }
        });
    }

    @Override
    public String getPageTitle() {
        return "View or Edit Container";
    }

    class ViewOrEditForm extends Form {
        ViewOrEditFormModel modelBean = new ViewOrEditFormModel();
        boolean enableAddress = true;
        IndicateModalSubmitLink formLink1;
        IndicateModalSubmitLink formLink2;

        public ViewOrEditForm(String id) {
            super(id);
            setDefaultModel(new CompoundPropertyModel(modelBean));
            add(new FeedbackPanel("feedback"));

            add((formLink1 = new IndicateModalSubmitLink("toggle") {
                @Override
                public void onSubmit() {
                    form.setEnabled(!form.isEnabled());
                }
                @Override
                public boolean isVisible() {
                    return isEnabled();
                }
            }));
            add((formLink2 = new IndicateModalSubmitLink("toggleAddr") {
                @Override
                public void onSubmit() {
                    form.enableAddress = !form.enableAddress;
                }
                @Override
                public boolean isVisible() {
                    return isEnabled();
                }
            }));

            add(new ViewOrEdit("name",  new TextField(ViewOrEdit.COMP_ID)));
            add(new ViewOrEdit("country",  new DropDown(ViewOrEdit.COMP_ID, countriesDS, true).setCharacterWidth(50), new ComponentModelToLabel<Country>() {
                public String convertToLabel(Country t) {
                    return t.getName()+" ("+t.getCode().toUpperCase()+")";
                }
            }));
            add(new ViewOrEdit("address",  new TextArea(ViewOrEdit.COMP_ID) {
                @Override
                public boolean isEnabled() {
                    return enableAddress;
                }
                @Override
                protected void onComponentTag(ComponentTag tag) {
                    super.onComponentTag(tag);
                    tag.put("rows", "5");
                    tag.put("cols", "60");
                }
            }));

            add(new IndicateModalSubmitLink("submit"));
        }

    }

    class ViewOrEditFormModel implements Serializable {
        private String name;
        private Country country;
        private String address;

        public ViewOrEditFormModel() {
            name = "John Smith";
            country = countries.get(32);
            address = "123 Fleet St\nLondon\nUnited Kingdom";
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Country getCountry() {
            return country;
        }

        public void setCountry(Country country) {
            this.country = country;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private void buildDataSource() {
        countriesDS = new DropDownDataSource<Country>() {
            public String getName() {
                return "countries";
            }
            public List<Country> getValues() {
                return countries;
            }

            public String getDescriptionForValue(Country t) {
                return t.getName()+" ("+t.getCode().toUpperCase()+")";
            }
        };
    }
}
