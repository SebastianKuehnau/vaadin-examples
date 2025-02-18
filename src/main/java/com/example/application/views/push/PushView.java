package com.example.application.views.push;

import com.example.application.services.BackendService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@Menu(icon = LineAwesomeIconUrl.ARROW_ALT_CIRCLE_UP, order = 2, title = "Vaadin Push")
@PageTitle("View with a slow backend")
@Route("push")
public class PushView extends VerticalLayout {


    public PushView(BackendService backendService) {

        var button = new Button("Start long-running task");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


        var notificationButton = new Button("Show notification", event -> {
            Notification notification = new Notification("Hello World!");
            notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
            notification.open();
        });
        notificationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        notificationButton.addClassNames(LumoUtility.Background.SUCCESS);

        button.addClickListener(event -> {
            //removed, as the result is only displayed when
            //the server is ready and the UI is blocked in the meantime
            //var result = backendService.fetchSlowly();
            //add(new Paragraph(result));

            event.getSource().getUI().ifPresent(ui -> {
                //call async services that returns a CompletableFuture
                backendService.fetchSlowlyAsync()
                    //invoke code after return
                    .thenAccept(result -> {
                        //access ui instance and add component
                        ui.access(() -> {
                            add(new Paragraph(result));
                        });
                    });
            });
        });

        add(new HorizontalLayout(button, notificationButton));
        setPadding(true);
    }
}
