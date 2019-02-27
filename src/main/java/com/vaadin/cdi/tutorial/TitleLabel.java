package com.vaadin.cdi.tutorial;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

@ViewScoped
public class TitleLabel extends Label {
		
	public TitleLabel() {
		this.addStyleName(ValoTheme.LABEL_BOLD);
		this.addStyleName(ValoTheme.LABEL_LARGE);
	}	
}
