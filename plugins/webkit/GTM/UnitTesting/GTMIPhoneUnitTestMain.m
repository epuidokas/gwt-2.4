//
//  GTMIPhoneUnitTestMain.m
//
//  Copyright 2008 Google Inc.
//
//  Licensed under the Apache License, Version 2.0 (the "License"); you may not
//  use this file except in compliance with the License.  You may obtain a copy
//  of the License at
// 
//  http://www.apache.org/licenses/LICENSE-2.0
// 
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
//  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
//  License for the specific language governing permissions and limitations under
//  the License.
//

#import "GTMDefines.h"
#if !GTM_IPHONE_SDK
  #error GTMIPhoneUnitTestMain for iPhone only
#endif
#import <objc/runtime.h>
#import <stdio.h>
#import <UIKit/UIKit.h>
#import "GTMSenTestCase.h"

// Used for sorting methods below
static int MethodSort(const void *a, const void *b) {
  const char *nameA = sel_getName(method_getName(*(Method*)a));
  const char *nameB = sel_getName(method_getName(*(Method*)b));
  return strcmp(nameA, nameB);
}

@interface UIApplication (iPhoneUnitTestAdditions)
// "Private" method that we need
- (void)terminate;
@end

@interface GTMIPhoneUnitTestDelegate : NSObject
@end

@implementation GTMIPhoneUnitTestDelegate

// Return YES if class is subclass (1 or more generations) of SenTestCase
- (BOOL)isTestFixture:(Class)aClass {
  BOOL iscase = NO;
  Class testCaseClass = [SenTestCase class];
  Class superclass;
  for (superclass = aClass; 
       !iscase && superclass; 
       superclass = class_getSuperclass(superclass)) {
    iscase = superclass == testCaseClass ? YES : NO;
  }
  return iscase;
}

// Log an error out to console in a way that Xcode will recognize it.
- (void)printError:(NSString *)error {
  if ([error rangeOfString:@"error:"].location == NSNotFound) {
    fprintf(stderr, "error: %s\n", [error UTF8String]);
  } else {
    fprintf(stderr, "%s\n", [error UTF8String]);
  }
  fflush(stderr);
}

// Run through all the registered classes and run test methods on any
// that are subclasses of SenTestCase.
- (void)applicationDidFinishLaunching:(UIApplication *)application {
  int count = objc_getClassList(NULL, 0);
  Class *classes = (Class*)malloc(sizeof(Class) * count);
  _GTMDevAssert(classes, @"Couldn't allocate class list");
  objc_getClassList(classes, count);
  int suiteSuccesses = 0;
  int suiteFailures = 0;
  int suiteTotal = 0;
  NSString *suiteName = [[NSBundle mainBundle] bundlePath];
  NSDate *suiteStartDate = [NSDate date];
  NSString *suiteStartString = [NSString stringWithFormat:@"Test Suite '%@' started at %@\n",
                                suiteName, suiteStartDate];
  fprintf(stderr, [suiteStartString UTF8String]);
  fflush(stderr);
  int i, j;
  for (i = 0; i < count; ++i) {
    Class currClass = classes[i];
    if ([self isTestFixture:currClass]) {
      NSDate *fixtureStartDate = [NSDate date];
      NSString *fixtureName = NSStringFromClass(currClass);
      NSString *fixtureStartString = [NSString stringWithFormat:@"Test Suite '%@' started at %@\n",
                                      fixtureName, fixtureStartDate];
      int fixtureSuccesses = 0;
      int fixtureFailures = 0;
      int fixtureTotal = 0;
      fprintf(stderr, [fixtureStartString UTF8String]);
      fflush(stderr);
      id testcase = [[currClass alloc] init];
      _GTMDevAssert(testcase, @"Unable to instantiate Test Suite: '%@'\n",
                    fixtureName);
      unsigned int methodCount;
      Method *methods = class_copyMethodList(currClass, &methodCount);
      // Sort our methods so they are called in Alphabetical order just
      // because we can.
      qsort(methods, methodCount, sizeof(Method), MethodSort);
      for (j = 0; j < methodCount; ++j) {
        Method currMethod = methods[j];
        SEL sel = method_getName(currMethod);
        const char *name = sel_getName(sel);
        // If it starts with test, run it.
        if (strstr(name, "test") == name) {
          fixtureTotal += 1;
          NSDate *caseStartDate = [NSDate date];
          BOOL failed = NO;
          @try {
            // Wrap things in autorelease pools because they may
            // have an STMacro in their dealloc which may get called
            // when the pool is cleaned up
            NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
            @try {
              [testcase setUp];
              [testcase performSelector:sel];
              [testcase tearDown];
              fixtureSuccesses += 1;
            } @catch (NSException *exception) {
              fixtureFailures += 1;
              failed = YES;
              [self printError:[exception reason]];
            }
            [pool release];
          } @catch (NSException *exception) {
            fixtureFailures += 1;
            failed = YES;
            [self printError:[exception reason]];
          }
          NSTimeInterval caseEndTime = [[NSDate date] timeIntervalSinceDate:caseStartDate];
          NSString *caseEndString = [NSString stringWithFormat:@"Test Case '-[%@ %s]' %s (%0.3f seconds).\n",
                                     fixtureName, name,
                                     failed ? "failed" : "passed", caseEndTime];
          fprintf(stderr, [caseEndString UTF8String]);
          fflush(stderr);
        }
      }
      if (methods) {
        free(methods);
      }
      [testcase release];
      NSDate *fixtureEndDate = [NSDate date];
      NSTimeInterval fixtureEndTime = [fixtureEndDate timeIntervalSinceDate:fixtureStartDate];
      NSString *fixtureEndString = [NSString stringWithFormat:@"Test Suite '%@' finished at %@.\n"
                                    "Executed %d tests, with %d failures (%d unexpected) in %0.3f seconds\n",
                                    fixtureName, fixtureEndDate, fixtureTotal, 
                                    fixtureFailures, fixtureFailures, fixtureEndTime];
      
      fprintf(stderr, [fixtureEndString UTF8String]);
      fflush(stderr);
      suiteTotal += fixtureTotal;
      suiteSuccesses += fixtureSuccesses;
      suiteFailures += fixtureFailures;      
    }
  }
  NSDate *suiteEndDate = [NSDate date];
  NSTimeInterval suiteEndTime = [suiteEndDate timeIntervalSinceDate:suiteStartDate];
  NSString *suiteEndString = [NSString stringWithFormat:@"Test Suite '%@' finished at %@.\n"
                              "Executed %d tests, with %d failures (%d unexpected) in %0.3f seconds\n",
                              suiteName, suiteEndDate, suiteTotal, 
                              suiteFailures, suiteFailures, suiteEndTime];
  fprintf(stderr, [suiteEndString UTF8String]);
  fflush(stderr);
  
  // Using private call to end our tests
  [[UIApplication sharedApplication] terminate];
}

@end

int main(int argc, char *argv[]) {
  NSAutoreleasePool * pool = [[NSAutoreleasePool alloc] init];
  int retVal = UIApplicationMain(argc, argv, nil, @"GTMIPhoneUnitTestDelegate");
  [pool release];
  return retVal;
}
