package com.example.application.views.positioning;

import com.flowingcode.vaadin.addons.syntaxhighlighter.ShLanguagePrism;
import com.flowingcode.vaadin.addons.syntaxhighlighter.ShStylePrism;
import com.flowingcode.vaadin.addons.syntaxhighlighter.SyntaxHighlighterPrism;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.addon.stefan.clipboard.ClientsideClipboard;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.text.MessageFormat;

/**
 * A view that demonstrates different component positioning options using FlexLayout.
 * This view allows users to experiment with different alignment and justification options
 * for both horizontal and vertical layouts.
 *
 * <pre>
 * Layout Structure:
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ PositioningView (VerticalLayout)                                        │
 * │ ┌─────────────────────────────────────────────────────────────────────┐ │
 * │ │ positioningLayout (HorizontalLayout)                                │ │
 * │ │ ┌────────────────┐  ┌───────────────────────────────────────┐       │ │
 * │ │ │                │  │ VerticalLayout                        │       │ │
 * │ │ │ justifyRadio   │  │ ┌───────────────────────────────────┐ │       │ │
 * │ │ │ ButtonGroup    │  │ │ orientationRadioButtonGroup       │ │       │ │
 * │ │ │                │  │ └───────────────────────────────────┘ │       │ │
 * │ │ │                │  │ ┌───────────────────────────────────┐ │       │ │
 * │ │ │                │  │ │ contentContainer (Div)            │ │       │ │
 * │ │ │                │  │ │ ┌───────────────────────────────┐ │ │       │ │
 * │ │ │                │  │ │ │ componentLayout (FlexLayout)  │ │ │       │ │
 * │ │ │                │  │ │ │  ┌─────┐  ┌─────┐  ┌─────┐    │ │ │       │ │
 * │ │ │                │  │ │ │  │Green│  │Blue │  │Red  │    │ │ │       │ │
 * │ │ │                │  │ │ │  │     │  │     │  │     │    │ │ │       │ │
 * │ │ │                │  │ │ │  └─────┘  └─────┘  └─────┘    │ │ │       │ │
 * │ │ │                │  │ │ └───────────────────────────────┘ │ │       │ │
 * │ │ │                │  │ └───────────────────────────────────┘ │       │ │
 * │ │ │                │  │ ┌───────────────────────────────────┐ │       │ │
 * │ │ │                │  │ │ alignRadioButtonGroup             │ │       │ │
 * │ │ │                │  │ └───────────────────────────────────┘ │       │ │
 * │ │ └────────────────┘  └───────────────────────────────────────┘       │ │
 * │ └─────────────────────────────────────────────────────────────────────┘ │
 * │ ┌─────────────────────────────────────────────────────────────────────┐ │
 * │ │ codeLayout (Details)                                                │ │
 * │ │ ┌─────────────────────────────────────────────────────────────────┐ │ │
 * │ │ │ syntaxHighlighter                                               │ │ │
 * │ │ └─────────────────────────────────────────────────────────────────┘ │ │
 * │ │ ┌─────────────────────────┐                                         │ │
 * │ │ │ copyToClipboardButton   │                                         │ │
 * │ │ └─────────────────────────┘                                         │ │
 * │ └─────────────────────────────────────────────────────────────────────┘ │
 * └─────────────────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * The view demonstrates:
 * <ul>
 *   <li>Different flex directions (ROW, COLUMN, ROW_REVERSE, COLUMN_REVERSE)</li>
 *   <li>Different justify content modes (START, CENTER, END, BETWEEN, AROUND, EVENLY)</li>
 *   <li>Different alignment options (START, CENTER, END, STRETCH, BASELINE)</li>
 * </ul>
 */
@Menu(icon = LineAwesomeIconUrl.ARROWS_ALT_SOLID, title = "Component Positioning")
@Route("")
@RouteAlias("positioning")
public class PositioningView extends VerticalLayout {

    public static final String CODE = """
            var componentLayout = new FlexLayout(createTestComponents());
            componentLayout.setFlexDirection(FlexLayout.FlexDirection.{0});
            componentLayout.setJustifyContentMode(JustifyContentMode.{1});
            componentLayout.setAlignItems(Alignment.{2});
            """;
    // Constants
    private static final String[] BORDER_CLASSES = {
            LumoUtility.Border.ALL,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.BorderColor.CONTRAST_10
    };
    private static final int LAYOUT_HEIGHT = 400;
    private static final int LAYOUT_WIDTH = 600;

    // UI Components
    private final FlexLayout componentLayout;
    private final RadioButtonGroup<Alignment> alignRadioButtonGroup;
    private final RadioButtonGroup<FlexLayout.FlexDirection> orientationRadioButtonGroup;
    private final RadioButtonGroup<JustifyContentMode> justifyRadioButtonGroup;

    private SyntaxHighlighterPrism syntaxHighlighter;
    private String formattedString;

    public PositioningView() {
        // Initialize components
        justifyRadioButtonGroup = createJustifyRadioGroup();

        orientationRadioButtonGroup = createOrientationRadioGroup();

        componentLayout = createComponentLayout();
        var contentContainer = new Div(componentLayout);
        contentContainer.setSizeFull();

        alignRadioButtonGroup = createAlignmentRadioGroup();

        var codeLayout = createCodeLayout();

        HorizontalLayout positioningLayout = new HorizontalLayout(
                justifyRadioButtonGroup,
                new VerticalLayout(
                        orientationRadioButtonGroup,
                        contentContainer,
                        alignRadioButtonGroup));
        positioningLayout.setAlignItems(Alignment.CENTER);
        add(positioningLayout, codeLayout);

        setAlignItems(Alignment.CENTER);
        setSizeFull();
    }

    private Details createCodeLayout() {

        syntaxHighlighter = new SyntaxHighlighterPrism(ShLanguagePrism.JAVA, "");
        syntaxHighlighter.setShStyle(ShStylePrism.OKAIDIA);
        syntaxHighlighter.setWidth(720, Unit.PIXELS);

        updateCode(FlexLayout.FlexDirection.ROW, JustifyContentMode.START, Alignment.START);

        Details codeDetails = new Details();
        codeDetails.setOpened(true);
        codeDetails.setSummaryText("Show Code");

        var copyToClipboardButton = new Button("Copy to Clipboard");
        copyToClipboardButton.setIcon(VaadinIcon.COPY_O.create());
        copyToClipboardButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        copyToClipboardButton.addClickListener(event -> {
            event.getSource().getUI().ifPresent(ui -> {
                ClientsideClipboard.writeToClipboard(formattedString, ui);
            });
        });

        codeDetails.add(syntaxHighlighter, copyToClipboardButton);
        codeDetails.setWidth(740, Unit.PIXELS);

        return codeDetails;
    }

    private void updateCode(FlexLayout.FlexDirection flexDirection, JustifyContentMode justifyContentMode, Alignment alignment) {
        formattedString = MessageFormat.format(CODE, flexDirection.toString(), justifyContentMode.toString(), alignment.toString());

        syntaxHighlighter.setContent(formattedString);
    }

    private FlexLayout createComponentLayout() {
        var componentLayout = new FlexLayout(createComponents());

        componentLayout.setFlexDirection(FlexLayout.FlexDirection.ROW);

        componentLayout.addClassNames(BORDER_CLASSES);
        componentLayout.setHeight(LAYOUT_HEIGHT, Unit.PIXELS);
        componentLayout.setWidth(LAYOUT_WIDTH, Unit.PIXELS);

        return componentLayout;
    }

    /**
     * Creates radio group for controlling orientation (row/column)
     */
    private RadioButtonGroup<FlexLayout.FlexDirection> createOrientationRadioGroup() {
        var radioGroup = new RadioButtonGroup<FlexLayout.FlexDirection>("Layout Orientation");
        radioGroup.setItems(FlexLayout.FlexDirection.values());
        radioGroup.setValue(FlexLayout.FlexDirection.ROW);
        radioGroup.addClassName(LumoUtility.Padding.MEDIUM);

        radioGroup.addValueChangeListener(event -> {
            componentLayout.setFlexDirection(event.getValue());

            componentLayout.setAlignItems(alignRadioButtonGroup.getValue());
            componentLayout.setJustifyContentMode(justifyRadioButtonGroup.getValue());

            handleStretchAlignment(alignRadioButtonGroup.getValue());

            updateCode(event.getValue(), justifyRadioButtonGroup.getValue(), alignRadioButtonGroup.getValue());

            // Refresh alignment options
            updateAlignmentOptions();
        });

        return radioGroup;
    }

    /**
     * Creates radio group for controlling justification
     */
    private RadioButtonGroup<JustifyContentMode> createJustifyRadioGroup() {
        var radioGroup = new RadioButtonGroup<JustifyContentMode>();
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setLabel("Justify Content Option");
        radioGroup.setItems(JustifyContentMode.values());
        radioGroup.setValue(JustifyContentMode.START);

        radioGroup.addValueChangeListener(event -> {
                    componentLayout.setJustifyContentMode(event.getValue());
                    updateCode(orientationRadioButtonGroup.getValue(), event.getValue(),  alignRadioButtonGroup.getValue());
                });

        return radioGroup;
    }

    /**
     * Creates radio group for controlling alignment
     */
    private RadioButtonGroup<Alignment> createAlignmentRadioGroup() {
        var radioGroup = new RadioButtonGroup<Alignment>();
        radioGroup.setLabel("Align Item Option");
        radioGroup.setItems(Alignment.values());
        radioGroup.setValue(Alignment.START);

        radioGroup.addValueChangeListener(event -> {
            recreateComponents();
            componentLayout.setAlignItems(event.getValue());
            handleStretchAlignment(event.getValue());
            updateCode(orientationRadioButtonGroup.getValue(), justifyRadioButtonGroup.getValue(), event.getValue());
        });

        return radioGroup;
    }

    /**
     * Creates test components to demonstrate alignment
     */
    private Component[] createComponents() {
        return new Component[] {
                createComponent("Green", LumoUtility.Background.SUCCESS, 36, 90, LumoUtility.Padding.Top.SMALL),
                createComponent("Blue", LumoUtility.Background.PRIMARY, 46, 110, LumoUtility.Padding.Top.MEDIUM),
                createComponent("Red", LumoUtility.Background.ERROR, 56, 130, LumoUtility.Padding.Top.XLARGE)
        };
    }

    /**
     * Create a single test component with given properties
     */
    private Component createComponent(String label, String backgroundColor, int height, int width, String paddingTop) {
        var div = new Div(label);

        div.addClassNames(
                backgroundColor,
                LumoUtility.Margin.MEDIUM,
                LumoUtility.Padding.SMALL,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.FontWeight.BOLD,
                paddingTop
        );

        div.setHeight(height, Unit.PIXELS);
        div.setWidth(width, Unit.PIXELS);

        return div;
    }

    /**
     * Updates alignment options based on current orientation
     */
    private void updateAlignmentOptions() {
        boolean isColumn = isColumnOrientation(orientationRadioButtonGroup.getValue());
        alignRadioButtonGroup.setItemEnabledProvider(alignment ->
                !(isColumn && alignment.equals(Alignment.BASELINE))
        );
    }

    /**
     * Check if the current flex direction is column-based
     */
    private static boolean isColumnOrientation(FlexLayout.FlexDirection flexDirection) {
        return flexDirection == FlexLayout.FlexDirection.COLUMN ||
                flexDirection == FlexLayout.FlexDirection.COLUMN_REVERSE;
    }

    /**
     * width or height can't be set when alignment is STRETCH
     */
    private void handleStretchAlignment(Alignment alignment) {
        boolean isStretch = alignment == Alignment.STRETCH;
        boolean isColumn = isColumnOrientation(componentLayout.getFlexDirection());

        if (isStretch) {
            componentLayout.getChildren()
                    .map(component -> (HasSize) component)
                    .forEach(hasSize -> {
                        if (!isColumn) {
                            hasSize.setHeight(null);
                        } else {
                            hasSize.setWidth(null);
                        }
                    });
        }
    }

    /**
     * Recreate all the test components in the flex layout
     */
    private void recreateComponents() {
        componentLayout.removeAll();
        componentLayout.add(createComponents());
    }
}