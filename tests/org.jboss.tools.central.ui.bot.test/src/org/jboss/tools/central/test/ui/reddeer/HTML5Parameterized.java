/*******************************************************************************
 * Copyright (c) 2017-2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.central.test.ui.reddeer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.matcher.RegexMatcher;
import org.eclipse.reddeer.common.util.Display;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.eclipse.condition.ServerModuleHasState;
import org.eclipse.reddeer.eclipse.core.resources.DefaultProject;
import org.eclipse.reddeer.eclipse.core.resources.Project;
import org.eclipse.reddeer.eclipse.m2e.core.ui.preferences.MavenSettingsPreferencePage;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.reddeer.eclipse.ui.problems.Problem;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView;
import org.eclipse.reddeer.eclipse.ui.views.markers.ProblemsView.ProblemType;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ModuleLabel;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.Server;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServerModule;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServersView2;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServersViewEnums.ServerState;
import org.eclipse.reddeer.eclipse.wst.server.ui.wizard.ModifyModulesDialog;
import org.eclipse.reddeer.junit.annotation.RequirementRestriction;
import org.eclipse.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.eclipse.reddeer.junit.requirement.matcher.RequirementMatcher;
import org.eclipse.reddeer.junit.runner.RedDeerSuite;
import org.eclipse.reddeer.requirements.jre.JRERequirement.JRE;
import org.eclipse.reddeer.requirements.server.ServerRequirementState;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.condition.ShellIsActive;
import org.eclipse.reddeer.swt.impl.browser.InternalBrowser;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.core.lookup.WorkbenchPartLookup;
import org.eclipse.reddeer.workbench.handler.WorkbenchShellHandler;
import org.eclipse.reddeer.workbench.impl.editor.DefaultEditor;
import org.eclipse.reddeer.workbench.impl.shell.WorkbenchShell;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.eclipse.ui.IViewReference;
import org.jboss.ide.eclipse.as.reddeer.server.deploy.DeployOnServer;
import org.jboss.ide.eclipse.as.reddeer.server.requirement.ServerRequirement.JBossServer;
import org.jboss.tools.central.reddeer.api.JavaScriptHelper;
import org.jboss.tools.central.reddeer.utils.CentralUtils;
import org.jboss.tools.central.reddeer.wizards.NewProjectExamplesWizardDialogCentral;
import org.jboss.tools.central.test.ui.reddeer.internal.CentralBrowserIsLoading;
import org.jboss.tools.central.test.ui.reddeer.internal.ErrorsReporter;
import org.jboss.tools.common.reddeer.utils.StackTraceUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * 
 * @author rhopp
 * @contributor jkopriva@redhat.com
 * @contributor vprusa@redhat.com
 * @contributor zcervink@redhat.com
 *
 */

@JRE(cleanup = true)
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
@JBossServer(state=ServerRequirementState.RUNNING)
public class HTML5Parameterized {

	private static final String CENTRAL_LABEL = "Red Hat Central";
	private static final String SEARCH_STRING = "eap-7.0.0.GA";
	private static final String MAVEN_SETTINGS_PATH = System.getProperty("maven.config.file") == null
			? "./target/classes/settings.xml"
			: System.getProperty("maven.config.file");
	private static final String BLACKLIST_ERRORS_REGEXES_FILE = "resources/blacklist-examples-errors-regexes.json";
	private JSONObject blacklistErrorsFileContents;
	
	
	private static String FULL_SERVER_NAME = "";
	private static DefaultEditor centralEditor;
	private static InternalBrowser browser;
	private static ErrorsReporter reporter = ErrorsReporter.getInstance();
	private static JavaScriptHelper jsHelper = JavaScriptHelper.getInstance();
	private static Logger log = new Logger(HTML5Parameterized.class);
	private ProjectExplorer projectExplorer;
	protected static final String VERSION = "version";

	@Parameters(name = "{0}")
	public static Collection<CentralProject> data() {
		closeWelcomeScreen();
		List<CentralProject> resultList = new ArrayList<CentralProject>();
		new DefaultToolItem(new WorkbenchShell(), CENTRAL_LABEL).click();
		centralEditor = new DefaultEditor(CENTRAL_LABEL);
		centralEditor.activate();
		browser = new InternalBrowser();
		jsHelper.setBrowser(browser);
		new WaitWhile(new CentralBrowserIsLoading(), TimePeriod.LONG);
		jsHelper.searchFor(SEARCH_STRING);
		do {
			String[] examples = jsHelper.getExamples();
			for (String exampleName : examples) {
				if (System.getProperty("specificQuickstarts") == null || System.getProperty("specificQuickstarts").isEmpty() || System.getProperty("specificQuickstarts").contains(exampleName)) {
					resultList.add(new CentralProject(exampleName, jsHelper.getDescriptionForExample(exampleName)));
				}
			}
			jsHelper.nextPage();
		} while (jsHelper.hasNext());
		return resultList;
	}

	private static void closeWelcomeScreen() {
		log.debug("Trying to close Welcome Screen");
		for (IViewReference viewReference : WorkbenchPartLookup.getInstance().findAllViewReferences()) {
			if (viewReference.getPartName().equals("Welcome")) {
				final IViewReference iViewReference = viewReference;
				Display.syncExec(new Runnable() {
					@Override
					public void run() {
						iViewReference.getPage().hideView(iViewReference);
					}
				});
				log.debug("Welcome Screen closed");
				break;
			}
		}
	}
	
	@RequirementRestriction
	public static Collection<RequirementMatcher> getRestrictionMatcher() {
		return Arrays.asList(new RequirementMatcher(JRE.class, VERSION, "1.8"));
	}

	@BeforeClass
	public static void setupClass() {
		closeWelcomeScreen();
		String mvnConfigFileName = new File(MAVEN_SETTINGS_PATH).getAbsolutePath();
		WorkbenchPreferenceDialog preferenceDialog = new WorkbenchPreferenceDialog();
		preferenceDialog.open();
		MavenSettingsPreferencePage prefPage = new MavenSettingsPreferencePage(preferenceDialog);
		preferenceDialog.select(prefPage);
		prefPage.setUserSettingsLocation(mvnConfigFileName);
		preferenceDialog.ok();
		ServersView2 serversView = new ServersView2();
		serversView.open();
		FULL_SERVER_NAME = getServerFullName();
	}

	@Before
	public void setup() {
		projectExplorer = new ProjectExplorer();
		projectExplorer.open();
	}

	@After
	public void teardown() {
		WorkbenchShellHandler.getInstance().closeAllNonWorbenchShells();
		//clean workspace
		new ProjectExplorer().deleteAllProjects(true);
		ConsoleView consoleView = new ConsoleView();
		consoleView.open();
		consoleView.clearConsole();
		//clean server
		ServersView2 serversView = new ServersView2();
		serversView.open();
		serversView.getServer(FULL_SERVER_NAME).clean();
		
		new DefaultToolItem(new WorkbenchShell(), CENTRAL_LABEL).click();
		// activate central editor
		new DefaultEditor(CENTRAL_LABEL);
	}

	@AfterClass
	public static void teardownClass() {
		reporter.generateReport();
	}

	@Parameter
	public CentralProject project;

	@Test
	public void testProject() {
		processExample(project.getName(), project.getDescription());
	}

	/**
	 * Imports current example, checks for warnings/errors, tries to deploy it to
	 * server.
	 * 
	 * @param exampleName, description
	 */
	private void processExample(String exampleName, String description) {
		
		log.error("Processing example: " + exampleName);
		log.error("\twith description: " + description);
		
		// import quickstart
		jsHelper.searchFor(description);
		try {
			importExample(exampleName);
		} catch (Exception e) {
			fail("Error importing example: " + StackTraceUtils.stackTraceToString(e));
			return;
		}

		// check errors in the Problems view
		if (!getProjectName().contains("crash")) {
			// TODO skip tests failing on purpose
			org.jboss.tools.central.reddeer.projects.Project currentProject;
			currentProject = new org.jboss.tools.central.reddeer.projects.Project(exampleName, getProjectName());
			
			// check for deployment-blocking errors
			// -> if all of them are blacklisted -> just skip the deployment
			// -> if at least one deployment-blocking errors is not blacklisted -> fail the test
			int deploymentBlockingErrorsCount = getDeploymentBlockingErrorsCount(currentProject);
			if (deploymentBlockingErrorsCount > 0) {
				return;
			}
		}
		
		// deploy the quickstart
		// multimodular project
		if (isProjectMultimodular(exampleName)) {
			List<Project> deployableModuls = findDeployableProjects();
			String topNodeProjectName = "jboss-" + exampleName + "-ear";

			Iterator<Project> dmi = deployableModuls.iterator();
			while (dmi.hasNext()) {
				Project proj = dmi.next();
				if (proj.getText().equals(topNodeProjectName)) {
					deployableModuls.remove(proj);
					break;
				}
			}

			DeployOnServer ds = new DeployOnServer();
			// deploy project
			ds.deployProject(topNodeProjectName, FULL_SERVER_NAME);
			// check deployed project
			checkDeployedProject(topNodeProjectName);
			// check deployed submodules
			checkDeployedSubmodules(topNodeProjectName, deployableModuls);
			// undeploy project
			ds.unDeployModule(topNodeProjectName, FULL_SERVER_NAME);
		}
		// the'inter-app' quickstart requires batch deployment and specific testing
		else if (exampleName.equals("inter-app")) {
			ServersView2 serversView = new ServersView2();
			serversView.open();
			Server server = serversView.getServers().get(0);

			ModifyModulesDialog mmd = server.addAndRemoveModules();
			new PushButton("Add All >>").click();
			mmd.finish();

			String[] deployableProjectsToTest = { "jboss-inter-app-appA", "jboss-inter-app-appB" };
			for (String project : deployableProjectsToTest) {
				DeployOnServer ds = new DeployOnServer();
				// deploy project
				ds.deployProject(project, FULL_SERVER_NAME);
				// check deployed project
				checkDeployedProject(project);
			}

			ModifyModulesDialog mmd2 = server.addAndRemoveModules();
			new PushButton("<< Remove All").click();
			mmd2.finish();
		}
		// other quickstarts
		else {
			for (Project project : findDeployableProjects()) {
				DeployOnServer ds = new DeployOnServer();
				// deploy project
				ds.deployProject(project.getName(), FULL_SERVER_NAME);
				// check deployed project
				checkDeployedProject(project.getName());
				// undeploy project
				ds.unDeployModule(project.getName(), FULL_SERVER_NAME);
			}
		}
	}

	private void importExample(String exampleName) {
		log.step("Importing example: " + exampleName);
		centralEditor.activate();
		jsHelper.clickExample(exampleName);
		NewProjectExamplesWizardDialogCentral wizardDialog = new NewProjectExamplesWizardDialogCentral();
		
		// issue JBIDE-27185
		if (exampleName.equals("template")) {
			try {
				wizardDialog.finish(exampleName);
				fail("issue JBIDE-27185 seems to be fixed");
			} catch (RuntimeException e) {
				new DefaultShell("Quick Fix");
				new PushButton("Finish").click();
				new WaitWhile(new ShellIsActive("New Project Example"));
				new WaitWhile(new JobIsRunning(), TimePeriod.VERY_LONG);
			}
		} else {
			wizardDialog.finish(exampleName);
		}
	}

	private int getDeploymentBlockingErrorsCount(org.jboss.tools.central.reddeer.projects.Project p) {
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		ProblemsView pv = new ProblemsView();
		pv.open();
		StringBuilder sb = new StringBuilder("Errors after project example import\n");
		boolean errorsArePresent = false;
		List<Problem> errorList = new ArrayList<Problem>();
		
		// get all errors from the Problems view
		for (Problem error : pv.getProblems(ProblemType.ERROR)) {
			errorList.add(error);
		}
		int errorsCount = errorList.size();
		
		// filter blacklisted errors
		Iterator<Problem> it = errorList.iterator();
		while (it.hasNext()) {
			Problem oo = it.next();
			if (oo.getResource().equals("README.md")) {
				it.remove();
				errorsCount--;
			}
			else if (isErrorOfSpecificQuickstartOnBlacklist(oo.getDescription(), p.getName())) {
				it.remove();
			}
		}
		
		// process errors
		for (Problem error : errorList) {
			sb.append(error.getDescription() + "\n");
			errorsArePresent = true;
		}
		for (Problem warning : pv.getProblems(ProblemType.WARNING)) {
			reporter.addWarning(p, warning.getDescription());
		}
		
		// fail the test if any not-blacklisted error remains
		if (errorsArePresent) {
			fail(sb.toString());
		}
		
		return errorsCount;
	}

	private String getProjectName() {
		projectExplorer.activate();
		List<DefaultProject> projects = projectExplorer.getProjects();
		return projects.get(0).getName();
	}
	
	private static String getServerFullName() {
		ServersView2 serversView = new ServersView2();
		serversView.open();
		for (Server srv : serversView.getServers()) {
			if (srv.getLabel().getName().contains("Enterprise Application Platform")) {
				return srv.getLabel().getName();
			}
		}
		return null;
	}

	private void checkDeployedProject(String projectName) {
		ServersView2 serversView = new ServersView2();
		serversView.open();
		ServerModule module = serversView.getServer(FULL_SERVER_NAME)
				.getModule(new RegexMatcher(".*" + projectName + ".*"));
		new WaitUntil(new ServerModuleHasState(module, ServerState.STARTED), TimePeriod.getCustom(30));
		ModuleLabel moduleLabel = module.getLabel();
		assertTrue("Module has not been started!", moduleLabel.getState() == ServerState.STARTED);
	}
	
	private ArrayList<Project> findDeployableProjects() {
		ArrayList<Project> projects = new ArrayList<Project>();
		projectExplorer.activate();
		for (Project project : projectExplorer.getProjects()) {
			projectExplorer.getProject(project.getName()).select();
			try {
				if (new ContextMenuItem("Run As", "1 Run on Server").isEnabled()) {
					projects.add(project);
				}
			} catch (CoreLayerException ex) {
				continue;// non deployable project
			}
		}
		return projects;
	}

	private static boolean isProjectMultimodular(String exampleName) {
		String[] multimoduleProject = { "app-client", "ejb-throws-exception", "kitchensink-ear", "kitchensink-ml-ear" };
		return ArrayUtils.contains(multimoduleProject, exampleName);
	}
	
	private static void checkDeployedSubmodules(String topNodeProjectName, List<Project> leafNodeProjects) {
		ServersView2 serversView = new ServersView2();
		serversView.open();
		Server server = serversView.getServer(FULL_SERVER_NAME);

		TreeItem serverTreeItem = server.getTreeItem();
		List<TreeItem> serverChildrenTreeItems = serverTreeItem.getItems();
		List<TreeItem> deployedSubmodulesTreeItems = new ArrayList<TreeItem>();
		for (TreeItem item : serverChildrenTreeItems) {
			if (item.getText().contains(topNodeProjectName)) {
				deployedSubmodulesTreeItems = item.getItems();
				break;
			}
		}

		for (Project module : leafNodeProjects) {
			boolean moduleDeployed = false;
			for (TreeItem item : deployedSubmodulesTreeItems) {
				if (item.getText().contains(module.getName())) {
					moduleDeployed = true;
				}
			}

			assertTrue("Deployable project from Project Explorer has not been found deployed in the Servers view.",
					moduleDeployed);
		}
	}
	
	/**
	 * Checks if the given error message description is in the regex blacklist file
	 */
	public boolean isErrorOfSpecificQuickstartOnBlacklist(String errorMsg, String quickstartName) {
		if (blacklistErrorsFileContents ==  null) {
			blacklistErrorsFileContents = CentralUtils.loadBlacklistErrorsFile(BLACKLIST_ERRORS_REGEXES_FILE);
		}

		JSONArray regexArray = (JSONArray) (blacklistErrorsFileContents).get(quickstartName);

		for (int i = 0; i < regexArray.size(); i++) {
			if (Pattern.compile((String) regexArray.get(i)).matcher(errorMsg).find()) {
				return true;
			}
		}

		return false;
	}
}
