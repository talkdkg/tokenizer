package org.tokenizer.ui.v7.modules;

import uk.co.q3c.v7.base.ui.V7UIModule;

import com.google.inject.multibindings.MapBinder;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

public class DemoUIModule extends V7UIModule {
	@Override
	protected void bindUIProvider() {
		bind(UIProvider.class).to(DemoUIProvider.class);
	}

	@Override
	protected void addUIBindings(MapBinder<String, UI> mapbinder) {
	    super.addUIBindings(mapbinder);
		mapbinder.addBinding(DemoUI.class.getName()).to(DemoUI.class);
	}
}
