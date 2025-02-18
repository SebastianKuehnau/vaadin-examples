package com.example.application.views.pagetitle;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

@Menu(order = 1, title = "Dynamic Page Title", icon = LineAwesomeIconUrl.NEWSPAPER)
//won't work with PageTitle annotation and implements HasDynamicTitle
//@PageTitle("Static Page Title")
@Route("pagetitle")
public class PageTitleView extends VerticalLayout
        implements HasDynamicTitle
{
    final SecureRandom rand = new SecureRandom();

    public PageTitleView() {
        var field = createIntegerFieldWithStepper();
        field.addValueChangeListener(event -> {
            UI.getCurrent()
                    .getPage()
                    .setTitle("Programmatically set title #" +
                            event.getValue());
        });
        add(field);
    }


    @Override
    public String getPageTitle() {
        return "Dynamic title #" + rand.nextInt(100);
    }

    private IntegerField createIntegerFieldWithStepper() {
        var field = new IntegerField("Choose a number:");
        field.setValue(42);
        field.setStepButtonsVisible(true);
        return field;
    }
}
