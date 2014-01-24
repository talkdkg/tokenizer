package org.tokenizer.ui;

import uk.co.q3c.util.ResourceUtils;
import uk.co.q3c.v7.base.navigate.sitemap.FileSitemapModule;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapFile;

public  class TestFileSitemapModule extends FileSitemapModule {

	@Override
	protected void define() {
		addEntry("a", new SitemapFile("/usr/java/tokenizer-home/sitemap.properties"));
	}

}
