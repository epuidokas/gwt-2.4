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
package com.google.gwt.user.cellview.client;

import com.google.gwt.view.client.TreeViewModel;

/**
 * Tests for {@link CellTree}.
 */
public class CellTreeTest extends AbstractCellTreeTestBase {

  public CellTreeTest() {
    super(false);
  }

  /**
   * Test that replacing a subset of children updates both the TreeNode value
   * and the underlying DOM correctly.
   */
  public void testReplaceChildren() {
    CellTree cellTree = (CellTree) tree;
    TreeNode root = cellTree.getRootTreeNode();

    // Open a couple of child nodes.
    TreeNode a = root.setChildOpen(0, true);
    TreeNode b = root.setChildOpen(1, true);
    assertEquals("a", a.getValue());
    assertEquals("ab", a.getChildValue(1));
    assertEquals("b", b.getValue());
    assertEquals("bc", b.getChildValue(2));

    // Replace "b" with a "new" value.
    model.getRootDataProvider().getList().set(1, "new");
    model.getRootDataProvider().flush();
    assertFalse(a.isDestroyed());
    assertTrue(b.isDestroyed());
    TreeNode newNode = root.setChildOpen(1, true);
    assertEquals("a", a.getValue());
    assertEquals("ab", a.getChildValue(1));
    assertEquals("new", newNode.getValue());
    assertEquals("newc", newNode.getChildValue(2));
    
    // Check the underlying DOM values.
    CellTreeNodeView<?> aImpl = cellTree.rootNode.getChildNode(0);
    CellTreeNodeView<?> newNodeImpl = cellTree.rootNode.getChildNode(1);
    assertEquals("a", aImpl.getCellParent().getInnerText());
    assertEquals(10, aImpl.ensureChildContainer().getChildCount());
    assertEquals("new", newNodeImpl.getCellParent().getInnerText());
  }

  public void testSetDefaultNodeSize() {
    CellTree cellTree = (CellTree) tree;
    TreeNode root = cellTree.getRootTreeNode();
    assertEquals(10, root.getChildCount());

    TreeNode b = root.setChildOpen(1, true);
    assertEquals(10, b.getChildCount());

    // Change the default size.
    cellTree.setDefaultNodeSize(5);
    assertEquals(5, cellTree.getDefaultNodeSize());
    assertEquals(10, b.getChildCount());
    TreeNode d = root.setChildOpen(3, true);
    assertEquals(5, d.getChildCount());
  }

  @Override
  protected <T> CellTree createAbstractCellTree(
      TreeViewModel model, T rootValue) {
    return new CellTree(model, rootValue);
  }
}
