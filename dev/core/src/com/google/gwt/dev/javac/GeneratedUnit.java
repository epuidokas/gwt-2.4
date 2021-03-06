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
package com.google.gwt.dev.javac;

/**
 * A generated Java compilation unit.
 */
public interface GeneratedUnit {
  long creationTime();

  String getSource();

  /**
   * Returns the source code as a token for {@link DiskCache#INSTANCE}, or -1 if
   * the source is not cached.
   */
  long getSourceToken();

  String getStrongHash();

  String getTypeName();

  String optionalFileLocation();
}
