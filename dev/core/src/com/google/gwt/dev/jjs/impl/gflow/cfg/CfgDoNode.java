/*
 * Copyright 2009 Google Inc.
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
package com.google.gwt.dev.jjs.impl.gflow.cfg;

import com.google.gwt.dev.jjs.ast.JDoStatement;
import com.google.gwt.dev.jjs.ast.JExpression;

/**
 * Node corresponding to while statement.
 */
public class CfgDoNode extends CfgConditionalNode<JDoStatement> {
  public CfgDoNode(CfgNode<?> parent, JDoStatement node) {
    super(parent, node);
  }

  @Override
  public void accept(CfgVisitor visitor) {
    visitor.visitDoNode(this);
  }

  @Override
  public JExpression getCondition() {
    return getJNode().getTestExpr();
  }

  @Override
  protected CfgNode<?> cloneImpl() {
    return new CfgDoNode(getParent(), getJNode());
  }
}
