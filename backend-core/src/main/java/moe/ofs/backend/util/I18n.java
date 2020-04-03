package moe.ofs.backend.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class I18n {
    /** the current selected Locale. */
    private static final ObjectProperty<Locale> locale;

    static {
        locale = new SimpleObjectProperty<>(Locale.getDefault());
        locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
    }

    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(Arrays.asList(Locale.ENGLISH, Locale.GERMAN));
    }

    public static Locale getDefaultLocale() {
        Locale sysDefault = Locale.getDefault();
        return getSupportedLocales().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
    }

    public static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(Locale locale) {
        localeProperty().set(locale);
        Locale.setDefault(locale);
    }
    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public static String getString(final ResourceBundle bundle, final Labeled labeled, final Object... args) {
        return String.format(bundle.getString(labeled.getId()), args);
    }

    public static String getString(final ResourceBundle bundle, final String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            log.warn("Unable to locate string with key: " + key +
                    " from ResourceBundle " + bundle.getBaseBundleName());
            return null;
        }
    }


    public static void toPaneOrNotToPane(Node node, ResourceBundle bundle) {
        if(node instanceof Pane) {
            ((Pane) node).getChildren().forEach(n -> toPaneOrNotToPane(n, bundle));
        } else {
            if(node instanceof Labeled) {
                try {
                    ((Labeled) node).setText(bundle.getString(node.getId()));
                } catch (Exception e) {
                    if(node.getId() != null && !node.getId().equals(""))
                        System.out.println("missing resource for Labeled fx:id -> " + node);
                    else
                        System.out.println("missing fx:id for Labeled " + node);
                }
            }

            else if(node instanceof TextInputControl) {
                try {
                    ((TextInputControl) node).setPromptText(bundle.getString(node.getId()));
                } catch (Exception e) {
                    if(node.getId() != null && !node.getId().equals(""))
                        System.out.println("missing resource for TextInputControl fx:id -> " + node);
                    else
                        System.out.println("missing fx:id for TextInputControl " + node);
                }
            }

            if(node instanceof SplitPane) {
                ((SplitPane) node).getItems().forEach(n -> toPaneOrNotToPane(n, bundle));
            }

            else if(node instanceof TabPane) {
                ((TabPane) node).getTabs().forEach(tab -> {
                    try {
                        tab.setText(bundle.getString(tab.getId()));
                    } catch (Exception e) {
                        if(tab.getId() != null && !tab.getId().equals(""))
                            System.out.println("missing resource for Tab fx:id -> " + tab);
                        else
                            System.out.println("missing fx:id for Tab " + tab);
                    }

                    toPaneOrNotToPane(tab.getContent(), bundle);
                });
            }

            else if(node instanceof Accordion) {
                ((Accordion) node).getPanes().forEach(p -> toPaneOrNotToPane(p, bundle));
            }

            // not a pane, but if it extends parent, then it has child elements
            else {
                if(node instanceof Parent) {
                    System.out.println("Parent node -> " + node);
                    ((Parent) node).getChildrenUnmodifiable().forEach(n -> toPaneOrNotToPane(n, bundle));
                } else {
                    System.out.println("unknown node = " + node);
                }
            }


        }
    }
}
