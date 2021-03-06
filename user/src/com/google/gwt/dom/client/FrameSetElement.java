/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dom.client;

/**
 * Create a grid of frames.
 * 
 * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/present/frames.html#edef-FRAMESET">W3C HTML Specification</a>
 */
@TagName(FrameSetElement.TAG)
public class FrameSetElement extends Element {

  static final String TAG = "frameset";

  /**
   * Assert that the given {@link Element} is compatible with this class and
   * automatically typecast it.
   */
  public static FrameSetElement as(Element elem) {
    assert elem.getTagName().equalsIgnoreCase(TAG);
    return (FrameSetElement) elem;
  }

  protected FrameSetElement() {
  }

  /**
   * The number of columns of frames in the frameset.
   * 
   * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/present/frames.html#adef-cols-FRAMESET">W3C HTML Specification</a>
   */
  public final native String getCols() /*-{
     return this.cols;
   }-*/;

  /**
   * The number of rows of frames in the frameset.
   * 
   * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/present/frames.html#adef-rows-FRAMESET">W3C HTML Specification</a>
   */
  public final native String getRows() /*-{
     return this.rows;
   }-*/;

  /**
   * The number of columns of frames in the frameset.
   * 
   * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/present/frames.html#adef-cols-FRAMESET">W3C HTML Specification</a>
   */
  public final native void setCols(String cols) /*-{
     this.cols = cols;
   }-*/;

  /**
   * The number of rows of frames in the frameset.
   * 
   * @see <a href="http://www.w3.org/TR/1999/REC-html401-19991224/present/frames.html#adef-rows-FRAMESET">W3C HTML Specification</a>
   */
  public final native void setRows(String rows) /*-{
     this.rows = rows;
   }-*/;
}
