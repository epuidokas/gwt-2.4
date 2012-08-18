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
package com.google.gwt.sample.expenses.shared;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

import java.util.Date;

/**
 * Report DTO.
 */
@ProxyFor(com.google.gwt.sample.expenses.server.domain.Report.class)
public interface ReportProxy extends EntityProxy {
  EmployeeProxy getApprovedSupervisor();

  Date getCreated();

  String getDepartment();

  /* 
   * TODO You shouldn't need to expose Ids like this.
   * Instead use EntityProxy.stableId() and RequestFactory.find() 
   */
  Long getId();

  String getNotes();

  String getPurpose();

  EmployeeProxy getReporter();

  void setApprovedSupervisor(EmployeeProxy approvedSupervisor);

  void setCreated(Date created);

  void setDepartment(String department);

  void setNotes(String notes);

  void setPurpose(String purpose);

  void setReporter(EmployeeProxy reporter);

  EntityProxyId<ReportProxy> stableId();
}
