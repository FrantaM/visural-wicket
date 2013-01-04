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
package com.visural.wicket.behavior.dateinput;

import com.visural.common.StringUtil;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

/**
 * Light-weight date input component.
 *
 * See http://plugins.jquery.com/project/date-input
 *
 *   * Requires JQuery
 *   * Month and day names will be picked up from bound component's `Locale`
 * 
 * @author Richard Nichols
 */
public class DateInputBehavior extends Behavior {
    private static final long serialVersionUID = 1L;
    
    private Set<Component> bound = new HashSet<Component>();
    private Map<String,String> optionMap = new HashMap<String,String>();

    public DateInputBehavior() {
    }

    /**
     * Override and return false to suppress static JavaScript and CSS contributions.
     * (May be desired if you are concatenating / compressing resources as part of build process)
     * @return
     */
    protected boolean autoAddToHeader() {
        return true;
    }

    @Override
    public void bind(Component component) {
        setLocale(component.getLocale());
        bound.add(component);
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component com, IHeaderResponse response) {
        if (autoAddToHeader()) {
        	response.render(CssHeaderItem.forReference(new DateInputCSSRef()));
        	response.render(JavaScriptReferenceHeaderItem.forReference(new DateInputJSRef()));
        }
        response.render(OnDomReadyHeaderItem.forScript(getJS()));
    }

    private String getJS() {
        StringBuilder js = new StringBuilder();
        for (Component com : bound) {
            js.append("jQuery(\"#")
              .append(com.getMarkupId())
              .append("\").date_input(")
              .append(getOptionMap())
              .append(");");
        }
        return js.toString();
    }

    private void setLocale(Locale locale) {
        if (!locale.getLanguage().equalsIgnoreCase("en")) {
            DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);
            setMonthNames(initCap(Arrays.asList(dfs.getMonths())));
            setShortMonthNames(initCap(Arrays.asList(dfs.getShortMonths())));
            String[] wkdays = dfs.getShortWeekdays();
            setShortDayNames(initCap(Arrays.asList(wkdays[Calendar.SUNDAY],
                    wkdays[Calendar.MONDAY],
                    wkdays[Calendar.TUESDAY],
                    wkdays[Calendar.WEDNESDAY],
                    wkdays[Calendar.THURSDAY],
                    wkdays[Calendar.FRIDAY],
                    wkdays[Calendar.SATURDAY]
                    )));
        }
    }

    private String initCap(String s) {
        if (s.length() < 2) {
            return s.toUpperCase();
        }
        return s.substring(0,1).toUpperCase()+s.substring(1);
    }
    
    private List<String> initCap(Collection<String> c) {
        ArrayList<String> result = new ArrayList<String>();
        for (String s : c) {
            result.add(initCap(s));
        }
        return result;
    }

    /**
     * Change the displayed date format for the control using the available
     * enumerated date formats.
     * 
     * @param format
     * @return
     */
    public DateInputBehavior setDateFormat(DateInputFormat format) {
        if (StringUtil.isNotBlankStr(format.getStrToDate())) {
            optionMap.put("stringToDate", format.getStrToDate());
        } else {
            optionMap.remove("stringToDate");
        }
        if (StringUtil.isNotBlankStr(format.getDateToStr())) {
            optionMap.put("dateToString", format.getDateToStr());
        } else {
            optionMap.remove("dateToString");
        }     
        return this;
    }

    /**
     * Change displayed months names, starting from January
     * @param months
     * @return
     */
    public DateInputBehavior setMonthNames(Collection<String> months) {
        optionMap.put("month_names", "[\""+StringUtil.delimitObjectsToString("\",\"", months)+"\"]");
        return this;
    }

    /**
     * Change displayed short months names, starting from January
     * @param months
     * @return
     */
    public DateInputBehavior setShortMonthNames(Collection<String> months) {
        optionMap.put("short_month_names", "[\""+StringUtil.delimitObjectsToString("\",\"", months)+"\"]");
        return this;
    }

    /**
     * Change displayed day names starting with Sunday
     * @param days
     * @return
     */
    public DateInputBehavior setShortDayNames(Collection<String> days) {
        optionMap.put("short_day_names", "[\""+StringUtil.delimitObjectsToString("\",\"", days)+"\"]");
        return this;
    }

    private String getOptionMap() {
        StringBuffer sb = new StringBuffer("{");
        boolean first = true;
        for (String key : optionMap.keySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append(key).append(":").append(optionMap.get(key));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}


/*
 *
 * How to use it

Note: If you are using a jQuery version less than 1.2.6, you will also need to install the Dimensions plugin.

Include jQuery (I am assuming you already know how to do that)
Include the Date Input plugin:

<script type="text/javascript" src="jquery.date_input.js"></script>
Include the CSS:

<link rel="stylesheet" href="date_input.css" type="text/css">
Fields are turned into date inputs by getting a jQuery object of the fields you want, and calling date_input() on then. I find it convenient to give all my date inputs a class of date_input and transform them automatically on DOM load. There is an initialize method which can do this for you, like so:

jQuery($.date_input.initialize);
If you want to get a bit more specific, you can do it like this:

jQuery(function() {
  jQuery("#my_specific_input").date_input();
});
Customisation

There is support for simple customisation without completely bloating the plugin. This works by letting you specify options when you call jQuery(el).date_input({my: opts}). The options essentially replace keys in the DateInput.prototype object so you can replace any internal method like this if you want. However, there are a few â€œrecommendedâ€� customisations you can make and I canâ€™t promise that other customisations wonâ€™t break in future versions.

Instead of specifying options for every new date input, you can specify global defaults by overwriting or modifying DateInput.DEFAULT_OPTS. These will then be used automatically.

Internationalisation

The only strings used by Date Input are month names (January, February, etc..), short month names (Jan, Feb, etcâ€¦) and short day names (Sun, Mon, etcâ€¦). The usual way to specify a new language is to change the default options. For example the Spanish translation is:

jQuery.extend(DateInput.DEFAULT_OPTS, {
  month_names: ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"],
  short_month_names: ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"],
  short_day_names: ["Dom", "Lun", "Mar", "MiÃ©", "Jue", "Vie", "Sab"]
});
Itâ€™s recommended to place this in a separate file called (using the example of a Spanish translation) jquery.date_input.es_ES.js. The currently available translations are:

Bosnian, Bosnia and Herzegovina
Bulgarian, Bulgaria
Chinese, China
Croatian, Croatia
Czech, Czech Republic â€“ Jiri Melcak
Danish, Denmark â€“ Jan Christensen
Dutch, The Netherlands â€“ Edwin Martin
English, Great Britain â€“ This is the default so there is no translation file
Estonian, Estonia â€“ Taimar Teetlok
French, France â€“ JÃ©rÃ´me TEISSEIRE
German, Germany â€“ Stefan Rado
Greek, Greece
Hebrew, Israel
Hungarian, Hungary â€“ HorvÃ¡th BalÃ¡zs
Indonesian, Indonesia
Italian, Italy
Norwegian, Norway
Polish, Poland â€“ Adam Kozubowicz
Portuguese, Portugal â€“ Jean Reis
Romanian, Romania â€“ Grigore Enescu Madalin
Russian, Russia â€“ Cyrill Udartcev
Slovak, Slovakia â€“ Erik MÃ¡rfÃ¶ldi
Spanish, Spain â€“ Sebastian Romano
Swedish, Sweden â€“ Christian Jarhult
Thai, Thailand â€“ Tanongsak Yingpadungsab
Turkish, Turkey
Ukrainian, Ukraine
If you have made a translation for a language not listed here, please see the section on contributing for instructions on how to get it uploaded.

First day of the week

The day names are listed from Sunday to Saturday, as this corresponds with JavaScriptâ€™s representation of days of the week being between 0 for Sunday and 6 for Saturday. The default first day of the week is 1 for Monday. You can change this in the options like so:

jQuery(el).date_input({ start_of_week: 0 });
or:

$.extend(DateInput.DEFAULT_OPTS, { start_of_week: 0 });
Date formatting

Date formatting is done by two methods: stringToDate, which takes a string and returns a JavaScript Date object, and dateToString which takes a JavaScript Date object and returns a string. You can replace these two functions in the options to format the date differently. For example, the following formats dates as YYYY-MM-DD:

$.extend(DateInput.DEFAULT_OPTS, {
  stringToDate: function(string) {
    var matches;
    if (matches = string.match(/^(\d{4,4})-(\d{2,2})-(\d{2,2})$/)) {
      return new Date(matches[1], matches[2] - 1, matches[3]);
    } else {
      return null;
    };
  },

  dateToString: function(date) {
    var month = (date.getMonth() + 1).toString();
    var dom = date.getDate().toString();
    if (month.length == 1) month = "0" + month;
    if (dom.length == 1) dom = "0" + dom;
    return date.getFullYear() + "-" + month + "-" + dom;
  }
});
Browser Compatibility

Date input is known to work with the following browsers:

Firefox 2.0
IE 7
IE 6
Safari 3 (tested on Windows, probably works on Mac too)
Opera 9
I only have access to Windows and Linux. If you find Date Input to work or not with the various Mac browsers, please let me know. Better still, send me a patch ;)
 */
