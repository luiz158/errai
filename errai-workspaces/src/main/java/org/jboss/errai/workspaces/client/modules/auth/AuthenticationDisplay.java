/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.errai.workspaces.client.modules.auth;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import org.gwt.mosaic.ui.client.Caption;
import org.gwt.mosaic.ui.client.MessageBox;
import org.gwt.mosaic.ui.client.WindowPanel;
import org.gwt.mosaic.ui.client.layout.BoxLayout;
import org.gwt.mosaic.ui.client.layout.LayoutPanel;

/**
 * A mosaic based based login box
 */
public class AuthenticationDisplay extends LayoutPanel
        implements AuthenticationModule.Display

{
    private TextBox userNameInput;
    private PasswordTextBox passwordInput;
    private Button loginButton;

    private WindowPanel windowPanel;

    public AuthenticationDisplay() {
        super();

        userNameInput = new TextBox();
        passwordInput = new PasswordTextBox();

        loginButton = new Button("Submit");

        createLayoutWindowPanel();
    }

  private void createLayoutWindowPanel() {
        windowPanel = new WindowPanel("Authentication required");
        windowPanel.setAnimationEnabled(false);
        LayoutPanel panel = new LayoutPanel();
        //panel.addStyleName("WSLogin");
        windowPanel.setWidget(panel);


        // create contents
        panel.setLayout(new BoxLayout(BoxLayout.Orientation.VERTICAL));
        Grid grid = new Grid(3, 2);
        grid.setWidget(0, 0, new Label("Username:"));
        grid.setWidget(0, 1, userNameInput);

        grid.setWidget(1, 0, new Label("Password:"));
        grid.setWidget(1, 1, passwordInput);

        grid.setWidget(2, 0, new HTML(""));
        grid.setWidget(2, 1, loginButton);

        /**
         * Create a handler so that striking enter automatically
         * submits the login.
         */
        KeyDownHandler clickOnEnter = new KeyDownHandler() {
            
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    loginButton.click();
                }
            }
        };

        userNameInput.addKeyDownHandler(clickOnEnter);
        passwordInput.addKeyDownHandler(clickOnEnter);

        /**
         * Close the window immediately upon submission.
         */
        loginButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                windowPanel.hide();
            }
        });

        panel.add(grid);

        windowPanel.getHeader().add(Caption.IMAGES.window().createImage());

        windowPanel.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> event) {
                windowPanel = null;
            }
        });
    }


    public void showLoginPanel() {
        if (null == windowPanel)
            createLayoutWindowPanel();

        clearPanel();
        windowPanel.pack();
        windowPanel.center();
    }


    public void clearPanel() {
        userNameInput.setText("");
        passwordInput.setText("");
    }


    public void hideLoginPanel() {
        if (windowPanel != null)
            windowPanel.hide();
    }


    public HasText getUsernameInput() {
        return userNameInput;
    }


    public HasText getPasswordInput() {
        return passwordInput;
    }


    public HasClickHandlers getSubmitButton() {
        return loginButton;
    }


    public HasCloseHandlers getWindowPanel() {
        return windowPanel;
    }


    public void showWelcomeMessage(final String messageText) {
        Timer t = new Timer() {

            public void run() {
                MessageBox.info("Welcome", messageText);
            }
        };

        t.schedule(500);
    }
}