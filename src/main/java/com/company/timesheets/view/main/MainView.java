package com.company.timesheets.view.main;

import com.company.timesheets.component.slider.Slider;
import com.company.timesheets.component.themetoggle.ThemeToggle;
import com.company.timesheets.entity.TimeEntry;
import com.company.timesheets.entity.User;
import com.company.timesheets.event.TimeEntryStatusChangedEvent;
import com.google.common.base.Strings;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.LoadContext;
import io.jmix.core.Messages;
import io.jmix.core.Metadata;
import io.jmix.core.usersubstitution.CurrentUserSubstitution;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.component.main.JmixListMenu;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;

@Route("")
@ViewController(id = "ts_MainView")
@ViewDescriptor(path = "main-view.xml")
public class MainView extends StandardMainView {

    @Autowired
    private Messages messages;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private CurrentUserSubstitution currentUserSubstitution;
    @Autowired
    private Metadata metadata;
    @Autowired
    private DataManager dataManager;
    @ViewComponent
    private JmixListMenu menu;
    @Autowired
    private Notifications notifications;

    @Install(to = "userMenu", subject = "buttonRenderer")
    private Component userMenuButtonRenderer(final UserDetails userDetails) {
        if (!(userDetails instanceof User user)) {
            return null;
        }

        String userName = generateUserName(user);

        Div content = uiComponents.create(Div.class);
        content.setClassName("user-menu-button-content");

        Avatar avatar = createAvatar(userName);

        Span name = uiComponents.create(Span.class);
        name.setText(userName);
        name.setClassName("user-menu-text");

        content.add(avatar, name);

        if (isSubstituted(user)) {
            Span subtext = uiComponents.create(Span.class);
            subtext.setText(messages.getMessage("userMenu.substituted"));
            subtext.setClassName("user-menu-subtext");

            content.add(subtext);
        }

        return content;
    }

    @Install(to = "userMenu", subject = "headerRenderer")
    private Component userMenuHeaderRenderer(final UserDetails userDetails) {
        if (!(userDetails instanceof User user)) {
            return null;
        }

        Div content = uiComponents.create(Div.class);
        content.setClassName("user-menu-header-content");

        String name = generateUserName(user);

        Avatar avatar = createAvatar(name);
        avatar.addThemeVariants(AvatarVariant.LUMO_LARGE);

        Span text = uiComponents.create(Span.class);
        text.setText(name);
        text.setClassName("user-menu-text");

        content.add(avatar, text);

        if (name.equals(user.getUsername())) {
            text.addClassNames("user-menu-text-subtext");
        } else {
            Span subtext = uiComponents.create(Span.class);
            subtext.setText(user.getUsername());
            subtext.setClassName("user-menu-subtext");

            content.add(subtext);
        }

        return content;
    }

    private Avatar createAvatar(String fullName) {
        Avatar avatar = uiComponents.create(Avatar.class);
        avatar.setName(fullName);
        avatar.getElement().setAttribute("tabindex", "-1");
        avatar.setClassName("user-menu-avatar");

        return avatar;
    }

    private String generateUserName(User user) {
        String userName = String.format("%s %s",
                        Strings.nullToEmpty(user.getFirstName()),
                        Strings.nullToEmpty(user.getLastName()))
                .trim();

        return userName.isEmpty() ? user.getUsername() : userName;
    }

    private boolean isSubstituted(User user) {
        UserDetails authenticatedUser = currentUserSubstitution.getAuthenticatedUser();
        return user != null && !authenticatedUser.getUsername().equals(user.getUsername());
    }

    @Subscribe
    public void onInit(final InitEvent event) {
        updateRejectedTimeEntries();
    }

    @EventListener
    private void timeEntryStatusChanged(TimeEntryStatusChangedEvent event){
        notifications.create("Rejected time entries were just updated.")
                .withDuration(0)
                .withCloseable(true)
                .withThemeVariant(NotificationVariant.LUMO_WARNING)
                .withPosition(Notification.Position.TOP_END)
                .show();
        updateRejectedTimeEntries();
    }

    private void updateRejectedTimeEntries() {
        LoadContext<TimeEntry> loadContext = new LoadContext<>(metadata.getClass(TimeEntry.class));
        loadContext.setQueryString(
                "select e from ts_TimeEntry e " +
                        "where e.user.username = :current_user_username " +
                        "and e.status = @enum(com.company.timesheets.entity.TimeEntryStatus.REJECTED)"
        );
        long count = dataManager.getCount(loadContext);

        Span badge = null;
        if (count > 0) {
            badge = new Span("" + count);
            badge.getElement().getThemeList().add("badge error");

        }
        menu.getMenuItem("ts_TimeEntries.my").setSuffixComponent(badge);
    }

    @Subscribe("themeToggle")
    public void onThemeToggleThemeToggleThemeChanged(final ThemeToggle.ThemeToggleThemeChangedEvent event) {
        notifications.show("Selected theme: " + event.getValue());
    }

    @Subscribe("slider")
    public void onSliderValueChanged(final Slider.SlideChangedEvent event) {
        notifications.show("Current value: " + event.getValue());
    }
}
