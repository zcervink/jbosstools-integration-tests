/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.test.utils;

import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ModuleLabel;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServerModule;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServersViewEnums.ServerPublishState;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServersViewEnums.ServerState;

public class ModuleIsInState extends AbstractWaitCondition {

	private ServerState state;
	private ServerPublishState publishState;
	private ServerModule module;

	public ModuleIsInState(ServerState state, ServerPublishState publishState, ServerModule module) {
		this.state = state;
		this.publishState = publishState;
		this.module = module;
	}

	@Override
	public boolean test() {
		ModuleLabel label = module.getLabel();
		return label.getState().equals(state) && label.getPublishState().equals(publishState);
	}
}
