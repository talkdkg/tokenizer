package org.tokenizer.ui.v7.modules;

import org.tokenizer.ui.v7.i18n.TokenizerLabelKeys;
import org.tokenizer.ui.v7.view.TaskInfoComponentView;
import org.tokenizer.ui.v7.view.UrlSearchComponentView;

import uk.co.q3c.v7.base.navigate.sitemap.DirectSitemapModule;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
public class AdminPages extends DirectSitemapModule {

	/**
	 * 
	 @see uk.co.q3c.v7.base.navigate.sitemap.DirectSitemapModule#define()
	 */
	@Override
	protected void define() {
	
	    addEntry("task-info", TaskInfoComponentView.class, TokenizerLabelKeys.TaskInfoComponent, PageAccessControl.AUTHENTICATION);

		addEntry("url-search", UrlSearchComponentView.class, TokenizerLabelKeys.UrlSearchComponent, PageAccessControl.AUTHENTICATION);

		
		//		addEntry("system-account/request-account", RequestSystemAccountCreateView.class, LabelKey.Request_Account,
//				PageAccessControl.PUBLIC);
//		addEntry("system-account/enable-account", RequestSystemAccountEnableView.class, LabelKey.Enable_Account,
//				PageAccessControl.PUBLIC);
//		addEntry("system-account/refresh-account", RequestSystemAccountRefreshView.class, LabelKey.Refresh_Account,
//				PageAccessControl.PUBLIC);
//		addEntry("system-account/reset-account", RequestSystemAccountResetView.class, LabelKey.Reset_Account,
//				PageAccessControl.PUBLIC);
//		addEntry("system-account/unlock-account", RequestSystemAccountUnlockView.class, LabelKey.Unlock_Account,
//				PageAccessControl.PUBLIC);

	}
}
