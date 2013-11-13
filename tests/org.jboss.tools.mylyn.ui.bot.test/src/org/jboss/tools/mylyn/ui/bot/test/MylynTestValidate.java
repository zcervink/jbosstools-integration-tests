package org.jboss.tools.mylyn.ui.bot.test;

/*
 * Prototype test for Mylyn
 * 
 * 
 */

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.condition.ShellWithTextIsActive;
import org.jboss.reddeer.swt.impl.menu.ShellMenu;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.wait.TimePeriod;
import org.jboss.reddeer.swt.wait.AbstractWait;
import org.jboss.reddeer.swt.wait.WaitUntil;
import org.jboss.reddeer.eclipse.mylyn.tasks.ui.view.*;
import org.jboss.reddeer.eclipse.ui.ide.RepoConnectionDialog;
//import org.jboss.reddeer.eclipse.mylyn.tasks.ui.wizards.RepoConnectionDialog;

public class MylynTestValidate {

	protected final Logger log = Logger.getLogger(this.getClass());
	
	protected ArrayList<String> expectedMylynElements = new ArrayList<String>();	
	protected final String TASKNAME = "a sample task in Mylyn";
	protected final String TASKNOTE = "a sample note for a sample task in Mylyn";

	@Test
	public void TestIt() {

		AbstractWait.sleep(TimePeriod.NORMAL.getSeconds());
		TaskRepositoriesView view = new TaskRepositoriesView();

		AbstractWait.sleep(TimePeriod.NORMAL.getSeconds());	
		view.open();

		AbstractWait.sleep(TimePeriod.NORMAL.getSeconds());		
		expectedMylynElements.add("Tasks");
		expectedMylynElements.add("Local");
		expectedMylynElements.add("Bugs");
		expectedMylynElements.add("Eclipse.org");
		expectedMylynElements.add("Red Hat Bugzilla");
		expectedMylynElements.add("Atlassian Integrations Support");
		expectedMylynElements.add("JBoss Community");
		
		List<TreeItem> repoItems = TestSupport.mylynTestSetup1(log);
		ArrayList<String> repoList = TestSupport.mylynTestSetup2(repoItems, log);
				
		for (String elementName : repoList) {
			assertTrue ("Mylyn element list incorrect - cannot find: " + elementName, expectedMylynElements.contains(elementName));
		}
		
		// JBDS50_0135 User can connect Bugzilla via Mylyn connectors plugin
		// JBDS50_0140 Red Hat Bugzilla task repository is available and can be
		// connected
		log.info("Step 4 - Validate connection to the Red Hat Bugzilla repo");

		int elementIndex = repoList.indexOf("Eclipse.org");
		log.info("Found: " + repoItems.get(elementIndex).getText());
		
		repoItems.get(elementIndex).select();	
		new ShellMenu("File", "Properties").select();  
	
		try {
			new WaitUntil(new ShellWithTextIsActive("Refreshing repository configuration"), TimePeriod.getCustom(60l)); 
		}
		catch (Exception E) {
			log.info ("Problem with 'Refreshing repository configuration' shell not seen");
		}		
			
		RepoConnectionDialog theRepoDialog = new RepoConnectionDialog();
		log.info(theRepoDialog.getText());

		AbstractWait.sleep(TimePeriod.NORMAL.getSeconds());
		theRepoDialog.validateSettings();

		log.info("["
				+ new LabeledText("Bugzilla Repository Settings").getText()
				+ "]");
		assertTrue("Repo Connection Properties Invalid",
				new LabeledText("Bugzilla Repository Settings").getText()
						.contains("Repository is valid"));
			
		theRepoDialog.finish();
		AbstractWait.sleep(TimePeriod.NORMAL.getSeconds());	
		
		view.close();
		
	} /* method */

} /* class */