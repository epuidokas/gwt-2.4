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
package org.hibernate.jsr303.tck.tests.constraints.customconstraint;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.util.UnitTestTreeLogger;

import static org.hibernate.jsr303.tck.util.TckGeneratorTestUtils.assertModuleFails;
import static org.hibernate.jsr303.tck.util.TckGeneratorTestUtils.getFullyQaulifiedModuleName;

import org.hibernate.jsr303.tck.tests.constraints.customconstraint.CustomConstraintValidatorTest.OddShoe;
import org.hibernate.jsr303.tck.util.TckCompileTestCase;

import javax.validation.UnexpectedTypeException;

/**
 * Test wrapper for {@link CustomConstraintValidatorTest} that are meant to fail
 * to compile.
 */
public class CustomConstraintValidatorCompileTest extends TckCompileTestCase {

  /**
   * Replacement for
   * {@link CustomConstraintValidatorTest#testUnexpectedTypeExceptionIsRaisedForInvalidType()}
   * 
   * @throws UnableToCompleteException
   */
  public void testUnexpectedTypeExceptionIsRaisedForInvalidType()
      throws UnableToCompleteException {
    UnitTestTreeLogger.Builder builder = new UnitTestTreeLogger.Builder();
    builder.expect(
        TreeLogger.ERROR,
        "No @org.hibernate.jsr303.tck.tests.constraints.customconstraint.Positive("
            + "message={validation.positive}, payload=[], groups=[]) "
            + "ConstraintValidator for type class java.lang.String",
        UnexpectedTypeException.class);
    builder.setLowestLogLevel(TreeLogger.INFO);
    UnitTestTreeLogger testLogger = builder.createLogger();
    assertModuleFails(testLogger,
        getFullyQaulifiedModuleName(getClass(), "TckCompileTest"),
        TckCompileTestValidatorFactory.GwtValidator.class, OddShoe.class);
  }
}
