package org.tokenizer;

import uk.co.q3c.v7.base.navigate.sitemap.DirectSitemapModule;
import uk.co.q3c.v7.base.shiro.PageAccessControl;
import uk.co.q3c.v7.i18n.LabelKey;

public class AdminPagesModule extends DirectSitemapModule
{

    /**
     * 
     @see uk.co.q3c.v7.base.navigate.sitemap.DirectSitemapModule#define()
     */
    @Override
    protected void define()
    {
        addEntry("task-info", DemoPrivateView.class, LabelKey.Small, PageAccessControl.AUTHENTICATION);
    }
}
