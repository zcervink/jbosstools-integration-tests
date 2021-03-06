/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.reddeer.thym.ui.wizard.project;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.tools.aerogear.reddeer.thym.ui.engine.EngineDownloadDialog;

public class EngineConfigurationPage extends WizardPage{
	
	public EngineConfigurationPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	public void selectEngine(ThymPlatform engine, String version){
		new DefaultTreeItem(engine.getText(), version).setChecked(true);
	}
	
	public List<ThymPlatform> getAvailableEngines(){
		List<ThymPlatform> availableEngines = new ArrayList<>();
		for(TreeItem i: new DefaultTree().getItems()){
			if(i.getText().equals(ThymPlatform.ANDROID.getText())){
				availableEngines.add(ThymPlatform.ANDROID);
				continue;
			}
			if(i.getText().equals(ThymPlatform.IOS.getText())){
				availableEngines.add(ThymPlatform.IOS);
				continue;
			}
			//add Windows
		}
		return availableEngines;
	}
	
	public List<String> getAvailableVersions(ThymPlatform engine){
		List<String> availableVersions = new ArrayList<>();
		DefaultTreeItem eng = new DefaultTreeItem(engine.getText());
		for(TreeItem i: eng.getItems()){
			availableVersions.add(i.getText());
		}
		return availableVersions;
	}
	
	public void addEngineVersion(ThymPlatform engine, String version){
		new PushButton("Add...").click();
		EngineDownloadDialog engineDownload = new EngineDownloadDialog();
		
		String v = version.substring(version.indexOf("@") + 1, version.length());
		engineDownload.selectEngine(engine.getText(),v);
		engineDownload.ok();
	}

}
