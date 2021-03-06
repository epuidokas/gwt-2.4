/*
 * Copyright 2010 Google Inc.
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

package com.google.gwt.dev.jjs;

import com.google.gwt.dev.jjs.test.CrossSiteIframeRunAsyncFailureTest;
import com.google.gwt.dev.jjs.test.CrossSiteIframeRunAsyncMetricsTest;
import com.google.gwt.dev.jjs.test.CrossSiteIframeRunAsyncTest;
import com.google.gwt.junit.tools.GWTTestSuite;

import junit.framework.Test;

/**
 * Tests that GWT.runAsync works with the cross-site iframe linker.
 */
public class CrossSiteIframeRunAsyncSuite {
  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("Test for runAsync with the cross-site iframe linker");

    suite.addTestSuite(CrossSiteIframeRunAsyncMetricsTest.class);
    suite.addTestSuite(CrossSiteIframeRunAsyncFailureTest.class);
    suite.addTestSuite(CrossSiteIframeRunAsyncTest.class);

    return suite;
  }
}
