package com.example.application.views.themevariant;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;

@PageTitle("Change Theme Variant in Flow")
@Menu(icon = LineAwesomeIconUrl.LIGHTBULB, title = "Theme Variants", order = 3)
@Route("theme-variant")
public class ThemeVariantView extends VerticalLayout {

    private final ComboBox<String> variantSelect = new ComboBox<>("Individual Theme Variants");
    private final RadioButtonGroup<String> themeSelect = new RadioButtonGroup<>("Lumo Theme Variants");

    public ThemeVariantView() {
        var name = createNameTextField();
        var showNotification = createNotificationButton(name);

        themeSelect.setItems(List.of(Lumo.LIGHT, Lumo.DARK));
        themeSelect.setValue(Lumo.LIGHT);

        themeSelect.addValueChangeListener(event -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            themeList.clear();
            themeList.add(event.getValue());
            variantSelect.clear();
        });

        variantSelect.setClearButtonVisible(true);
        var individualThemes = List.of("red", "green", "blue");
        variantSelect.setItems(individualThemes);

        variantSelect.addValueChangeListener(event -> {
                ThemeList themeList = UI.getCurrent().getElement().getThemeList();
                individualThemes.forEach(themeList::remove);
                themeList.add(event.getValue());
        });

        HorizontalLayout layout = new HorizontalLayout(name, showNotification);
        layout.setAlignItems(Alignment.BASELINE);

        add(layout, themeSelect, variantSelect);
    }

    private static Button createNotificationButton(TextField name) {
        Button showNotification = new Button("Show Notification", event -> Notification.show("Hello " + name.getValue()));
        showNotification.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return showNotification;
    }

    private static TextField createNameTextField() {
        return new TextField("Name");
    }
}
