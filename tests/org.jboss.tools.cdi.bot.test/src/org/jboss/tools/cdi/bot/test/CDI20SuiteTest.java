/*******************************************************************************
 * Copyright (c) 2019-2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.bot.test;

import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.cdi.bot.test.beans.bean.cdi20.AsYouTypeValidationTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.bean.cdi20.BeanValidationQuickFixTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.bean.cdi20.NullValuesInjectionTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.bean.cdi20.VetoedAnnotationTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.decorator.cdi20.DecoratorFromWebBeanTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.decorator.cdi20.DecoratorValidationQuickFixTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.dialog.cdi20.AllAssignableDialogTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.dialog.cdi20.AssignableDialogFilterTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.ibinding.cdi20.IBindingValidationQuickFixTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.interceptor.cdi20.InterceptorValidationQuickFixTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.named.cdi20.NamedComponentsSearchingTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.named.cdi20.NamedRefactoringTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.openon.cdi20.BeanInjectOpenOnTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.openon.cdi20.FindObserverEventTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.qualifier.cdi20.QualifierValidationQuickFixTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.scope.cdi20.ScopeValidationQuickFixTestCDI20;
import org.jboss.tools.cdi.bot.test.beans.stereotype.cdi20.StereotypeValidationQuickFixTestCDI20;
import org.jboss.tools.cdi.bot.test.beansxml.annotation.cdi20.BeanParametersAnnotationTestCDI20;
import org.jboss.tools.cdi.bot.test.beansxml.bean.cdi20.ExcludeBeanTestCDI20;
import org.jboss.tools.cdi.bot.test.beansxml.cdi20.BeansXMLBeansEditorTestCDI20;
import org.jboss.tools.cdi.bot.test.beansxml.cdi20.BeansXMLDiscoveryModesTestCDI20;
import org.jboss.tools.cdi.bot.test.beansxml.cdi20.BeansXMLUITestCDI20;
import org.jboss.tools.cdi.bot.test.beansxml.cdi20.BeansXMLValidationTestCDI20;
import org.jboss.tools.cdi.bot.test.beansxml.completion.cdi20.BeansXMLCompletionTestCDI20;
import org.jboss.tools.cdi.bot.test.beansxml.discovery.cdi20.BeanDiscoveryInExplicitArchivesTestCDI20;
import org.jboss.tools.cdi.bot.test.beansxml.discovery.cdi20.BeanDiscoveryInImplicitArchivesTestCDI20;
import org.jboss.tools.cdi.bot.test.beansxml.openon.cdi20.BeansXMLOpenOnTestCDI20;
import org.jboss.tools.cdi.bot.test.beansxml.validation.cdi20.BeansXMLAsYouTypeValidationTestCDI20;
import org.jboss.tools.cdi.bot.test.beansxml.validation.cdi20.BeansXMLValidationQuickFixTestCDI20;
import org.jboss.tools.cdi.bot.test.validation.cdi20.CDIValidatorTestCDI20;
import org.jboss.tools.cdi.bot.test.weld.cdi20.WeldBuiltInContextsTestCDI20;
import org.jboss.tools.cdi.bot.test.weld.cdi20.WeldExcludeTestCDI20;
import org.jboss.tools.cdi.bot.test.weld.cdi20.WeldParametersAnnotationTestCDI20;
import org.jboss.tools.cdi.bot.test.weld.cdi20.WeldScanTestCDI20;
import org.jboss.tools.cdi.bot.test.wizard.cdi20.CDIWebProjectWizardTestCDI20;
import org.jboss.tools.cdi.bot.test.wizard.cdi20.DynamicWebProjectWithCDITestCDI20;
import org.jboss.tools.cdi.bot.test.wizard.cdi20.EjbProjectWithCDITestCDI20;
import org.jboss.tools.cdi.bot.test.wizard.cdi20.UtilityProjectWithCDITestCDI20;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/** 
 * 
 * @author zcervink@redhat.com
 * 
 */
@RunWith(RedDeerSuite.class)
@SuiteClasses({
	NullValuesInjectionTestCDI20.class,
})
public class CDI20SuiteTest {

}
