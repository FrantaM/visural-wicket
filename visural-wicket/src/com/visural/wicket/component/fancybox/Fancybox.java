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
package com.visural.wicket.component.fancybox;

import com.visural.wicket.util.images.ImageReference;
import com.visural.common.Function;
import com.visural.common.StringUtil;
import com.visural.wicket.security.IPrivilege;
import com.visural.wicket.security.ISecureEnableInstance;
import com.visural.wicket.security.ISecureRenderInstance;
import java.util.HashMap;
import java.util.Map;
import net.fancybox.FancyBoxCSSRef;
import net.fancybox.FancyBoxJavascriptRef;
import net.fancybox.JQueryMouseWheelJSRef;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * A wicket-integration of the Fancybox component from http://fancybox.net/
 *
 * Apply to a `<a href="">...</a>` anchor tag.
 *
 * Used in the following way: `<a href="#" wicket:id="myBox">...</a>`
 *
 * Fancybox can display an image, another markup container (e.g. div) or an iframe
 * with a URL as a pop-up model box.
 *
 * Typically it would be used as a "lightbox" image viewer component, but videos
 * or other embedded media may also be used.
 *
 * Fancyboxes can be added to groups, however only the first element added to the
 * group's settings will take effect. i.e. the first element in the group's settings
 * will apply to the whole group. This is a limitation of the underlying plugin.
 *
 * When applying fancybox to another WebMarkupContainer, you should enclose the
 * container in a hidden div, e.g.
 *   `<div style="display: none;"><div wicket:id="myContentToShow">....</div></div>`
 * This is less optimal but has changed due to Fancybox changes in the latest version.
 *
 * @version $Id: Fancybox.java 261 2011-03-08 20:53:16Z tibes80@gmail.com $
 * @author Richard Nichols
 */
public class Fancybox extends WebMarkupContainer implements ISecureEnableInstance, ISecureRenderInstance {
    private static final long serialVersionUID = 1L;
    /**
     * Use this javascript to close an iframe fancybox from the child iframe code.
     */
    public static final String IFRAME_PARENT_CLOSE_JS = "parent.$.fancybox.close();";

    private FancyboxGroup group;
    private final ImageReference image;
    private final IModel iframeURL;
    private final WebMarkupContainer container;
    private String boxTitle;

    public Fancybox(String id, ImageReference image) {
        super(id);
        this.image = image;
        this.iframeURL = null;
        this.container = null;
        initAll();
    }

    /**
     * Create a fancy box link to an iframe URL.
     * @param id
     * @param linkText
     * @param iframeURL
     */
    public Fancybox(String id, IModel iframeURL) {
        super(id);
        this.image = null;
        this.iframeURL = iframeURL;
        this.container = null;
        initAll();
    }

    /**
     * Create a fancy box link to an iframe URL.
     * @param id
     * @param linkText
     * @param iframeURL
     */
    public Fancybox(String id, final String iframeURL) {
        super(id);
        this.image = null;
        this.iframeURL = new AbstractReadOnlyModel() {

            @Override
            public Object getObject() {
                return iframeURL;
            }
        };
        this.container = null;
        initAll();
    }

    /**
     * Create a fancy box link to another DOM element
     * @param id
     * @param linkText
     * @param iframeURL
     */
    public Fancybox(String id, WebMarkupContainer container) {
        super(id);
        this.image = null;
        this.iframeURL = null;
        this.container = container;
        initAll();
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
    protected void onComponentTag(ComponentTag tag) {
        checkComponentTag(tag, "a");
        super.onComponentTag(tag);
        if (boxTitle != null) {
            tag.put("title", boxTitle);
        }
        if (group != null) {
            tag.put("rel", group.getName());
        }
        if (isImage()) {
            tag.put("href", image.getURL());
        } else if (isIframe()) {
            tag.put("href", Function.nvl(this.iframeURL.getObject(), "").toString());
        } else if (isDiv()) {
            if (!container.getOutputMarkupId()) {
                throw new IllegalStateException("The container component " + container.getId() + " is not getOutputMarkupId() == true.");
            }
            tag.put("href", "#" + container.getMarkupId());
        }
    }

    private boolean isImage() {
        return (this.image != null);
    }

    private boolean isIframe() {
        return (this.iframeURL != null);
    }

    private boolean isDiv() {
        return (this.container != null);
    }

    private void initAll() {
        this.setOutputMarkupId(true);
        // we default the "hide on content click" to false if this isn't an image being displayed.
        if (!this.isImage()) {
            this.setHideOnContentClick(false);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (autoAddToHeader()) {
        	response.render(JavaScriptReferenceHeaderItem.forReference(new FancyBoxJavascriptRef()));
            if (isMouseWheelEnabled()) {
            	response.render(JavaScriptReferenceHeaderItem.forReference(new JQueryMouseWheelJSRef()));
            }
            response.render(CssHeaderItem.forReference(new FancyBoxCSSRef()));
        }
        String js = getFancyBoxJS();
        if (js != null) {
        	response.render(OnDomReadyHeaderItem.forScript(js));
        }
    }

    private String getFancyBoxJS() {
        if (group != null && group.getMain() != this) {
            return null;
        }
        String selector = (group != null ? group.getGroupSelector() : "#" + getMarkupId());
        String options = getBoxOptionsAsString();
        if (StringUtil.isBlankStr(options)) {
            return "jQuery('" + selector + "').fancybox();";
        } else {
            return "jQuery('" + selector + "').fancybox({" + options + "});";
        }
    }


    public FancyboxGroup getGroup() {
        return group;
    }

    public Fancybox setGroup(FancyboxGroup group) {
        if (this.group != null) {
            this.group.removeFancybox(this);
        }
        this.group = group;
        group.addFancybox(this);
        return this;
    }

    public String getBoxTitle() {
        return boxTitle;
    }

    public Fancybox setBoxTitle(String boxTitle) {
        this.boxTitle = boxTitle;
        return this;
    }

    /**
     * Return whether to enable mouse wheel scroll on fancybox groups.
     *
     * By default mouse wheel functionality is disabled as it increases the
     * amount of JavaScript required and only applies to box groups.
     *
     * Override and return true to enable.
     *
     * @return
     */
    public boolean isMouseWheelEnabled() {
        return false;
    }

    private String getBoxOptionsAsString() {
        StringBuilder result = new StringBuilder();
        Map<String, String> options = getBoxOptions();
        if (isIframe()) {
            // force iframe, otherwise have to use element class, which creates styling issues
            options.put("type", "'iframe'");
        }
        if (isImage()) {
            options.put("type", "'image'");
        }
        boolean addBreak = false;
        for (String option : options.keySet()) {
            if (addBreak) {
                result.append(",\n");
            }
            result.append("'");
            result.append(option);
            result.append("': ");
            result.append(options.get(option));
            addBreak = true;
        }
        return result.toString();
    }

    private Map<String, String> getBoxOptions() {
        HashMap<String, String> result = new HashMap<String, String>();
        if (padding != null) {
            result.put("padding", padding.toString());
        }
        if (margin != null) {
            result.put("margin", margin.toString());
        }
        if (opacity != null) {
            result.put("opacity", opacity.toString());
        }
        if (modal != null) {
            result.put("modal", modal.toString());
        }
        if (cyclic != null) {
            result.put("cyclic", cyclic.toString());
        }
        if (scrolling != null) {
            result.put("scrolling", scrolling.toString());
        }
        if (width != null) {
            result.put("width", width.toString());
        }
        if (height != null) {
            result.put("height", height.toString());
        }
        if (autoScale != null) {
            result.put("autoScale", autoScale.toString());
        }
        if (autoDimensions != null) {
            result.put("autoDimensions", autoDimensions.toString());
        }
        if (centerOnScroll != null) {
            result.put("centerOnScroll", centerOnScroll.toString());
        }
        if (hideOnOverlayClick != null) {
            result.put("hideOnOverlayClick", hideOnOverlayClick.toString());
        }
        if (hideOnContentClick != null) {
            result.put("hideOnContentClick", hideOnContentClick.toString());
        }
        if (overlayShow != null) {
            result.put("overlayShow", overlayShow.toString());
        }
        if (overlayOpacity != null) {
            result.put("overlayOpacity", overlayOpacity.toString());
        }
        if (titleShow != null) {
            result.put("titleShow", titleShow.toString());
        }
        if (speedIn != null) {
            result.put("speedIn", speedIn.toString());
        }
        if (speedOut != null) {
            result.put("speedOut", speedOut.toString());
        }
        if (changeSpeed != null) {
            result.put("changeSpeed", changeSpeed.toString());
        }
        if (showCloseButton != null) {
            result.put("showCloseButton", showCloseButton.toString());
        }
        if (showNavArrows != null) {
            result.put("showNavArrows", showNavArrows.toString());
        }
        if (enableEscapeButton != null) {
            result.put("enableEscapeButton", enableEscapeButton.toString());
        }
        if (overlayColor != null) {
            result.put("overlayColor", "'" + overlayColor + "'");
        }
        if (titlePosition != null) {
            result.put("titlePosition", "'" + titlePosition.getValue() + "'");
        }
        if (transitionIn != null) {
            result.put("transitionIn", "'" + transitionIn + "'");
        }
        if (transitionOut != null) {
            result.put("transitionOut", "'" + transitionOut + "'");
        }
        if (changeFade != null) {
            result.put("changeFade", "'" + changeFade + "'");
        }
        if (easingIn != null) {
            result.put("easingIn", "'" + easingIn + "'");
        }
        if (easingOut != null) {
            result.put("easingOut", "'" + easingOut + "'");
        }
        if (onStart != null) {
            result.put("onStart", "function() {" + onStart + "}");
        }
        if (onCancel != null) {
            result.put("onCancel", "function() {" + onCancel + "}");
        }
        if (onComplete != null) {
            result.put("onComplete", "function() {" + onComplete + "}");
        }
        if (onCleanup != null) {
            result.put("onCleanup", "function() {" + onCleanup + "}");
        }
        if (onClosed != null) {
            result.put("onClosed", "function() {" + onClosed + "}");
        }
        return result;
    }

    // fancybox properties follow
    //ajax	{ }	Ajax options (error, success will be overwritten)
    //swf	{wmode: 'transparent'}	Flashvars to put on the swf object
    //titleFormat	null	Callback to customize title area. You can set any html - custom image counter or even custom navigation
    private Integer padding; //	10	Space between FancyBox wrapper and content
    private Integer margin; //	20	Space between viewport and FancyBox wrapper
    private Boolean opacity; //	false	When true, transparency of content is changed for elastic transitions
    private Boolean modal; //	false	When true, 'overlayShow' is set to 'true' and 'hideOnOverlayClick', 'hideOnContentClick', 'enableEscapeButton', 'showCloseButton' are set to 'false'
    private Boolean cyclic; //	false	When true, galleries will be cyclic, allowing you to keep pressing next/back.
    private Boolean scrolling; //	'auto'	Set the overflow CSS property to create or hide scrollbars. Can be set to 'auto', 'yes', or 'no'
    private Integer width; //	560	Width for content types 'iframe' and 'swf'. Also set for inline content if 'autoDimensions' is set to 'false'
    private Integer height; //	340	Height for content types 'iframe' and 'swf'. Also set for inline content if 'autoDimensions' is set to 'false'
    private Boolean autoScale; //	true	If true, FancyBox is scaled to fit in viewport
    private Boolean autoDimensions; //	true	For inline and ajax views, resizes the view to the element recieves. Make sure it has dimensions otherwise this will give unexpected results
    private Boolean centerOnScroll; //	false	When true, FancyBox is centered while scrolling page
    private Boolean hideOnOverlayClick; //	true	Toggle if clicking the overlay should close FancyBox
    private Boolean hideOnContentClick; //	false	Toggle if clicking the content should close FancyBox
    private Boolean overlayShow; //	true	Toggle overlay
    private Float overlayOpacity; //	0.3	Opacity of the overlay (from 0 to 1; default - 0.3)
    private Boolean titleShow; //	true	Toggle title
    private String overlayColor; //	'#666'	Color of the overlay
    private TitlePosition titlePosition; //	'outside'	The position of title. Can be set to 'outside', 'inside' or 'over'
    private String transitionIn; //
    private String transitionOut; //	'fade'	The transition type. Can be set to 'elastic', 'fade' or 'none'
    private String changeFade; //	'fast'	Speed of the content fading while changing gallery items
    private String easingIn; //
    private String easingOut; //	'swing'	Easing used for elastic animations
    private String onStart; //	null	Will be called right before attempting to load the content
    private String onCancel; //	null	Will be called after loading is canceled
    private String onComplete; //	null	Will be called once the content is displayed
    private String onCleanup; //	null	Will be called just before closing
    private String onClosed; //	null	Will be called once FancyBox is closed
    private Integer speedIn; //
    private Integer speedOut; //	300	Speed of the fade and elastic transitions, in milliseconds
    private Integer changeSpeed; //	300	Speed of resizing when changing gallery items, in milliseconds
    private Boolean showCloseButton; //	true	Toggle close button
    private Boolean showNavArrows; //	true	Toggle navigation arrows
    private Boolean enableEscapeButton; //	true	Toggle if pressing Esc button closes FancyBox

    public Integer getPadding() {
        return padding;
    }

    /**
     * Space between FancyBox wrapper and content
     * @param padding
     * @return
     */
    public Fancybox setPadding(Integer padding) {
        this.padding = padding;
        return this;
    }

    public Integer getMargin() {
        return margin;
    }

    /**
     * Space between viewport and FancyBox wrapper
     * @param margin
     * @return
     */
    public Fancybox setMargin(Integer margin) {
        this.margin = margin;
        return this;
    }

    public Boolean getOpacity() {
        return opacity;
    }

    /**
     * When true, transparency of content is changed for elastic transitions
     * @param opacity
     * @return
     */
    public Fancybox setOpacity(Boolean opacity) {
        this.opacity = opacity;
        return this;
    }

    public Boolean getModal() {
        return modal;
    }

    /**
     * When true, 'overlayShow' is set to 'true' and 'hideOnOverlayClick', 
     * 'hideOnContentClick', 'enableEscapeButton', 'showCloseButton' are set
     * to 'false'
     * @param modal
     * @return
     */
    public Fancybox setModal(Boolean modal) {
        this.modal = modal;
        return this;
    }

    public Boolean getCyclic() {
        return cyclic;
    }

    /**
     * When true, galleries will be cyclic, allowing you to keep pressing next/back.
     * @param cyclic
     * @return
     */
    public Fancybox setCyclic(Boolean cyclic) {
        this.cyclic = cyclic;
        return this;
    }

    public Boolean getScrolling() {
        return scrolling;
    }

    /**
     * Set the overflow CSS property to create or hide scrollbars. Can be set to 'auto', 'yes', or 'no'
     * @param scrolling
     * @return
     */
    public Fancybox setScrolling(Boolean scrolling) {
        this.scrolling = scrolling;
        return this;
    }

    public Integer getWidth() {
        return width;
    }

    /**
     * Width for content types 'iframe' and 'swf'. Also set for inline content if 'autoDimensions' is set to 'false'
     * @param width
     * @return
     */
    public Fancybox setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    /**
     * Height for content types 'iframe' and 'swf'. Also set for inline content if 'autoDimensions' is set to 'false'
     * @param height
     * @return
     */
    public Fancybox setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public Boolean getAutoScale() {
        return autoScale;
    }

    /**
     * If true, FancyBox is scaled to fit in viewport 
     * @param autoScale
     * @return
     */
    public Fancybox setAutoScale(Boolean autoScale) {
        this.autoScale = autoScale;
        return this;
    }

    public Boolean getAutoDimensions() {
        return autoDimensions;
    }

    /**
     * For inline and ajax views, resizes the view to the element recieves.
     * Make sure it has dimensions otherwise this will give unexpected results
     * @param autoDimensions
     * @return
     */
    public Fancybox setAutoDimensions(Boolean autoDimensions) {
        this.autoDimensions = autoDimensions;
        return this;
    }

    public Boolean getCenterOnScroll() {
        return centerOnScroll;
    }

    /**
     * When true, FancyBox is centered while scrolling page
     * @param centerOnScroll
     * @return
     */
    public Fancybox setCenterOnScroll(Boolean centerOnScroll) {
        this.centerOnScroll = centerOnScroll;
        return this;
    }

    public Boolean getHideOnOverlayClick() {
        return hideOnOverlayClick;
    }

    /**
     * Toggle if clicking the overlay should close FancyBox
     * @param hideOnOverlayClick
     * @return
     */
    public Fancybox setHideOnOverlayClick(Boolean hideOnOverlayClick) {
        this.hideOnOverlayClick = hideOnOverlayClick;
        return this;
    }

    public Boolean getHideOnContentClick() {
        return hideOnContentClick;
    }

    /**
     * Toggle if clicking the content should close FancyBox
     * @param hideOnContentClick
     * @return
     */
    public Fancybox setHideOnContentClick(Boolean hideOnContentClick) {
        this.hideOnContentClick = hideOnContentClick;
        return this;
    }

    public Boolean getOverlayShow() {
        return overlayShow;
    }

    /**
     * Toggle overlay
     * @param overlayShow
     * @return
     */
    public Fancybox setOverlayShow(Boolean overlayShow) {
        this.overlayShow = overlayShow;
        return this;
    }

    public Float getOverlayOpacity() {
        return overlayOpacity;
    }

    /**
     * Opacity of the overlay (from 0 to 1; default - 0.3)
     * @param overlayOpacity
     * @return
     */
    public Fancybox setOverlayOpacity(Float overlayOpacity) {
        this.overlayOpacity = overlayOpacity;
        return this;
    }

    public String getOverlayColor() {
        return overlayColor;
    }

    /**
     * Color of the overlay
     * @param overlayColor
     * @return
     */
    public Fancybox setOverlayColor(String overlayColor) {
        this.overlayColor = overlayColor;
        return this;
    }

    public Boolean getTitleShow() {
        return titleShow;
    }

    /**
     * Toggle title
     * @param titleShow
     * @return
     */
    public Fancybox setTitleShow(Boolean titleShow) {
        this.titleShow = titleShow;
        return this;
    }

    public TitlePosition getTitlePosition() {
        return titlePosition;
    }

    /**
     * The position of title. Can be set to 'outside', 'inside' or 'over'
     * @param titlePosition
     * @return
     */
    public Fancybox setTitlePosition(TitlePosition titlePosition) {
        this.titlePosition = titlePosition;
        return this;
    }

    public FancyboxTransition getTransitionIn() {
        return transitionIn == null ? null : FancyboxTransition.valueOf(transitionIn);
    }

    /**
     * The transition type. Can be set to 'elastic', 'fade' or 'none'
     * @param transitionIn
     * @return
     */
    public Fancybox setTransitionIn(FancyboxTransition transitionIn) {
        this.transitionIn = transitionIn.name();
        return this;
    }

    public FancyboxTransition getTransitionOut() {
        return transitionOut == null ? null : FancyboxTransition.valueOf(transitionOut);
    }

    /**
     * The transition type. Can be set to 'elastic', 'fade' or 'none'
     * @param transitionOut
     * @return
     */
    public Fancybox setTransitionOut(FancyboxTransition transitionOut) {
        this.transitionOut = transitionOut.name();
        return this;
    }

    public Integer getSpeedIn() {
        return speedIn;
    }

    /**
     * Speed of the fade and elastic transitions, in milliseconds
     * @param speedIn
     * @return
     */
    public Fancybox setSpeedIn(Integer speedIn) {
        this.speedIn = speedIn;
        return this;
    }

    public Integer getSpeedOut() {
        return speedOut;
    }

    /**
     * Speed of the fade and elastic transitions, in milliseconds
     * @return
     */
    public Fancybox setSpeedOut(Integer speedOut) {
        this.speedOut = speedOut;
        return this;
    }

    public Integer getChangeSpeed() {
        return changeSpeed;
    }

    /**
     * Speed of resizing when changing gallery items, in milliseconds
     * @return
     */
    public Fancybox setChangeSpeed(Integer changeSpeed) {
        this.changeSpeed = changeSpeed;
        return this;
    }

    public String getChangeFade() {
        return changeFade;
    }

    /**
     * Speed of the content fading while changing gallery items
     * @param changeFade
     * @return
     */
    public Fancybox setChangeFade(String changeFade) {
        this.changeFade = changeFade;
        return this;
    }

    public String getEasingIn() {
        return easingIn;
    }

    /**
     * Easing used for elastic animations
     * @param easingIn
     * @return
     */
    public Fancybox setEasingIn(String easingIn) {
        this.easingIn = easingIn;
        return this;
    }

    public String getEasingOut() {
        return easingOut;
    }

    /**
     * Easing used for elastic animations
     * @param easingIn
     * @return
     */
    public Fancybox setEasingOut(String easingOut) {
        this.easingOut = easingOut;
        return this;
    }

    public Boolean getShowCloseButton() {
        return showCloseButton;
    }

    /**
     * Toggle close button
     * @param showCloseButton
     * @return
     */
    public Fancybox setShowCloseButton(Boolean showCloseButton) {
        this.showCloseButton = showCloseButton;
        return this;
    }

    public Boolean getShowNavArrows() {
        return showNavArrows;
    }

    /**
     * Toggle navigation arrows
     * @param showNavArrows
     * @return
     */
    public Fancybox setShowNavArrows(Boolean showNavArrows) {
        this.showNavArrows = showNavArrows;
        return this;
    }

    public Boolean getEnableEscapeButton() {
        return enableEscapeButton;
    }

    /**
     * Toggle if pressing Esc button closes FancyBox
     * @param enableEscapeButton
     * @return
     */
    public Fancybox setEnableEscapeButton(Boolean enableEscapeButton) {
        this.enableEscapeButton = enableEscapeButton;
        return this;
    }

    public String getOnStart() {
        return onStart;
    }

    /**
     * Will be called right before attempting to load the content
     * @param onStart
     * @return
     */
    public Fancybox setOnStart(String onStart) {
        this.onStart = onStart;
        return this;
    }

    public String getOnCancel() {
        return onCancel;
    }

    /**
     * Will be called after loading is canceled
     * @param onCancel
     * @return
     */
    public Fancybox setOnCancel(String onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public String getOnComplete() {
        return onComplete;
    }

    /**
     * Will be called once the content is displayed
     * @param onComplete
     * @return
     */
    public Fancybox setOnComplete(String onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    public String getOnCleanup() {
        return onCleanup;
    }

    /**
     * Will be called just before closing
     * @param onCleanup
     * @return
     */
    public Fancybox setOnCleanup(String onCleanup) {
        this.onCleanup = onCleanup;
        return this;
    }

    public String getOnClosed() {
        return onClosed;
    }

    /**
     * Will be called once FancyBox is closed
     * @param onClosed
     * @return
     */
    public Fancybox setOnClosed(String onClosed) {
        this.onClosed = onClosed;
        return this;
    }

    public IPrivilege getRenderPrivilege() {
        return IPrivilege.NULL;
    }

    public IPrivilege getEnablePrivilege() {
        return IPrivilege.NULL;
    }

}
