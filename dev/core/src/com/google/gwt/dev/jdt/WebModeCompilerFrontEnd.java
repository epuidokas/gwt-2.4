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
package com.google.gwt.dev.jdt;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.dev.javac.ArtificialRescueChecker;
import com.google.gwt.dev.jdt.FindDeferredBindingSitesVisitor.MessageSendSite;
import com.google.gwt.dev.jjs.impl.TypeLinker;
import com.google.gwt.dev.util.Empty;
import com.google.gwt.dev.util.log.speedtracer.CompilerEventType;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger;
import com.google.gwt.dev.util.log.speedtracer.SpeedTracerLogger.Event;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides a reusable front-end based on the JDT compiler that incorporates
 * GWT-specific concepts such as JSNI and deferred binding.
 */
public class WebModeCompilerFrontEnd extends BasicWebModeCompiler {

  public static CompilationResults getCompilationUnitDeclarations(TreeLogger logger,
      String[] seedTypeNames, RebindPermutationOracle rebindPermOracle, TypeLinker linker,
      ICompilationUnit... additionalUnits) throws UnableToCompleteException {
    Event getCompilationUnitsEvent =
        SpeedTracerLogger.start(CompilerEventType.GET_COMPILATION_UNITS);
    CompilationResults results =
        new WebModeCompilerFrontEnd(rebindPermOracle, linker).getCompilationUnitDeclarations(
            logger, seedTypeNames, additionalUnits);
    getCompilationUnitsEvent.end();
    return results;
  }

  private final RebindPermutationOracle rebindPermOracle;

  /**
   * Construct a WebModeCompilerFrontEnd.
   */
  private WebModeCompilerFrontEnd(RebindPermutationOracle rebindPermOracle, TypeLinker linker) {
    super(rebindPermOracle.getCompilationState(), linker);
    this.rebindPermOracle = rebindPermOracle;
  }

  @Override
  protected String[] doFindAdditionalTypesUsingArtificialRescues(TreeLogger logger,
      CompilationUnitDeclaration cud) {
    List<String> types = ArtificialRescueChecker.collectReferencedTypes(cud);
    return types.isEmpty() ? Empty.STRINGS : types.toArray(new String[types.size()]);
  }

  /**
   * Pull in types implicitly referenced through rebind answers.
   */
  @Override
  protected String[] doFindAdditionalTypesUsingRebinds(TreeLogger logger,
      CompilationUnitDeclaration cud) {
    Set<String> dependentTypeNames = new LinkedHashSet<String>();

    // Find all the deferred binding request types.
    FindDeferredBindingSitesVisitor v = new FindDeferredBindingSitesVisitor();
    cud.traverse(v, cud.scope);
    Map<String, MessageSendSite> requestedTypes = v.getSites();
    Map<String, String[]> rebindAnswers = new HashMap<String, String[]>();
    boolean doFinish = false;

    // For each, ask the host for every possible deferred binding answer.
    for (Map.Entry<String, MessageSendSite> entry : requestedTypes.entrySet()) {
      String reqType = entry.getKey();
      MessageSendSite site = entry.getValue();
      try {
        String[] resultTypes = rebindPermOracle.getAllPossibleRebindAnswers(logger, reqType);
        rebindAnswers.put(reqType, resultTypes);
        Collections.addAll(dependentTypeNames, resultTypes);
        doFinish = true;
      } catch (UnableToCompleteException e) {
        FindDeferredBindingSitesVisitor.reportRebindProblem(site, "Failed to resolve '" + reqType
            + "' via deferred binding");
        rebindAnswers.put(reqType, new String[0]);
      }
    }

    if (doFinish) {
      rebindPermOracle.getGeneratorContext().finish(logger);
    }

    // Sanity check all rebind answers.
    for (Map.Entry<String, MessageSendSite> entry : requestedTypes.entrySet()) {
      String reqType = entry.getKey();
      MessageSendSite site = entry.getValue();
      String[] resultTypes = rebindAnswers.get(reqType);
      // Check that each result is instantiable.
      for (String typeName : resultTypes) {
        checkRebindResultInstantiable(site, typeName);
      }
    }

    return dependentTypeNames.toArray(new String[dependentTypeNames.size()]);
  }

  private void checkRebindResultInstantiable(MessageSendSite site, String typeName) {
    /*
     * This causes the compiler to find the additional type, possibly winding
     * its back to ask for the compilation unit from the source oracle.
     */
    ReferenceBinding type = resolvePossiblyNestedType(typeName);

    // Sanity check rebind results.
    if (type == null) {
      FindDeferredBindingSitesVisitor.reportRebindProblem(site, "Rebind result '" + typeName
          + "' could not be found");
      return;
    }
    if (!type.isClass()) {
      FindDeferredBindingSitesVisitor.reportRebindProblem(site, "Rebind result '" + typeName
          + "' must be a class");
      return;
    }
    if (type.isAbstract()) {
      FindDeferredBindingSitesVisitor.reportRebindProblem(site, "Rebind result '" + typeName
          + "' cannot be abstract");
      return;
    }
    if (type.isNestedType() && !type.isStatic()) {
      FindDeferredBindingSitesVisitor.reportRebindProblem(site, "Rebind result '" + typeName
          + "' cannot be a non-static nested class");
      return;
    }
    if (type.isLocalType()) {
      FindDeferredBindingSitesVisitor.reportRebindProblem(site, "Rebind result '" + typeName
          + "' cannot be a local class");
      return;
    }
    // Look for a noArg ctor.
    MethodBinding noArgCtor = type.getExactConstructor(Binding.NO_PARAMETERS);
    if (noArgCtor == null) {
      FindDeferredBindingSitesVisitor.reportRebindProblem(site, "Rebind result '" + typeName
          + "' has no default (zero argument) constructors");
      return;
    }
  }
}
