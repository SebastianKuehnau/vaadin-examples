package com.example.application.views.webnotification;

import com.example.application.services.BackendService;
import com.example.application.services.BackendService.SalesData;
import com.example.application.services.WebPushService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.webpush.WebPush;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.text.NumberFormat;
import java.time.Month;
import java.util.List;
import java.util.Locale;

@PageTitle("Web Push Notification")
@Menu(icon = LineAwesomeIconUrl.ARROW_ALT_CIRCLE_UP_SOLID, title = "Web Notification", order = 5)
@Route("web-notification")
public class WebNotificationView extends VerticalLayout {

    private final WebPush webpush;
    private final WebPushService webPushService;

    private final Checkbox subscribed;
    private final ProgressBar progressBar = new ProgressBar();

    private final Div content = new Div();

    /***
     * The entire installation process is described here: <a href="https://vaadin.com/docs/latest/flow/configuration/webpush">...</a>
     *
     * Here is a summary of the steps needed:
     * 1. Installation of VAPID Keys (<a href="https://vaadin.com/docs/latest/flow/configuration/webpush#vapid-keys">...</a>)
     * 2. add flow-webpush dependency (<a href="https://vaadin.com/docs/latest/flow/configuration/webpush#web-push-dependencies">...</a>)
     * 3. add PWA annotation to Appliaction Shell Class to create Service Worker (<a href="https://vaadin.com/docs/latest/flow/configuration/webpush#web-push-dependencies">...</a>)
     * 4. create service class with WebPush instance to manage the subscriptions (@see {@link WebPushService})
     * 5. subscribe to web push registration
     * 6. add custom information to notification (@see in records in {@link WebPushService})
     * 7. process custom information on client side in frontend/sw.ts (see function focusOrOpenWindow)
     * 8. send out notification
     *
     ***/

    public WebNotificationView(WebPushService webPushService, BackendService backendService) {
        this.webPushService = webPushService;
        webpush = webPushService.getWebPush();

        WebPush webpush = webPushService.getWebPush();

        content.setSizeFull();

        subscribed = new Checkbox("Send Web Notification when heavy stuff is ready!");
        subscribed.addValueChangeListener(event ->
                event.getSource().getUI().ifPresent(ui -> {
                    //step 5 register and unregister the UI instance to the web push service
                    if (event.getValue())
                        webpush.subscribe(ui, webPushService::store);
                    else
                        webpush.unsubscribe(ui, webPushService::remove);
                }));

        Grid<SalesData> grid = new Grid<>();
        grid.addColumn(SalesData::productName)
                .setHeader("Product Name")
                .setAutoWidth(true);
        for (Month month : List.of(Month.JANUARY)) {
            grid.addColumn(getSalesDataLitRenderer(month))
                    .setAutoWidth(true)
                    .setTextAlign(ColumnTextAlign.CENTER)
                    .setHeader(month.name());
        }
        grid.setHeight("100%");

        var heavyButton = new Button("Do heavy stuff!");
        heavyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        heavyButton.addClickListener(event -> {
            event.getSource().getUI().ifPresent(ui -> {
                //call async services that returns a CompletableFuture
                backendService.generateRandomSalesData()
                        //invoke code after return
                        .thenAccept(salesDataList -> {
                            //access ui instance and add component
                            ui.access(() -> {

                                //step 7: send notifiation to subscribed clients
                                webPushService.notifyAll("Message from your Vaadin App", "Heavy Work is ready, so please come back!");

                                grid.setItems(salesDataList);

                                content.add(grid);
                                progressBar.setVisible(false);
                                heavyButton.setEnabled(true);
                            });
                        });
            });
            progressBar.setVisible(true);
            heavyButton.setEnabled(false);
            content.removeAll();
        });

        progressBar.setWidth(100, Unit.PIXELS);
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);

        HorizontalLayout buttonLayout = new HorizontalLayout(heavyButton, progressBar);
        buttonLayout.setAlignItems(Alignment.CENTER);
        add(buttonLayout, subscribed, content);
        setSizeFull();
    }

    private LitRenderer<SalesData> getSalesDataLitRenderer(Month month) {
        return LitRenderer.<SalesData>of("<div style=\"display: flex; justify-content: center;\">" +
                        "<vaadin-icon style=\"color: ${item.color};\" icon=\"lumo:arrow-${item.direction}\"></vaadin-icon>" +
                        "<div style=\"padding: 0 5px 0 5px; width: 75px; text-align: right;\">${item.value}</div>" +
                        "</div>")
                .withProperty("color", salesData -> getTrendColor(salesData.trendPerMonth().get(month.name())))
                .withProperty("direction", salesData -> getTrendDirection(salesData.trendPerMonth().get(month.name())))
                .withProperty("value", salesData ->
                        NumberFormat
                                .getCurrencyInstance(Locale.of("fi", "FI"))
                                .format(salesData.salesPerMonth().get(month.name())));
    }

    private String getTrendDirection(String trend) {
            return trend.equals("rising") ? "up" : trend.equals("falling") ? "down" : "right";
        }

    private String getTrendColor(String trend) {
        return trend.equals("rising") ? "green" : trend.equals("falling") ? "red" : "black";
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();
        //show if registration still exist
        webpush.subscriptionExists(ui, registered -> {
            subscribed.setValue(registered);
            if(registered && webPushService.isEmpty()) {
                webpush.fetchExistingSubscription(ui, webPushService::store);
            }
        });
    }
}
