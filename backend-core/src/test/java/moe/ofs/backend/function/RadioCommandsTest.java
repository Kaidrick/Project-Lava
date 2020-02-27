package moe.ofs.backend.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static moe.ofs.backend.function.RadioCommands.*;

class RadioCommandsTest {

    static final int GROUP_ID = 739;
    static final String ROOT_MENU = "menu in root";
    static final String MENU = "menu";
    static final String SUBMENU = "submenu";
    static final String ANOTHER_ROOT_MENU = "another menu in root";

    RadioControl rootMenu;
    RadioControl menu;
    RadioControl subMenu;
    RadioControl anotherRootMenu;

    @BeforeEach
    void setUp() {
        rootMenu = newRadioControl(GROUP_ID, ROOT_MENU);
        menu = newRadioControl(GROUP_ID, MENU, rootMenu);
        subMenu = newRadioControl(GROUP_ID, SUBMENU, menu);

        anotherRootMenu = newRadioControl(GROUP_ID, ANOTHER_ROOT_MENU, null);
    }

    @Test
    void getParentTable() {
        assertEquals("nil", RadioCommands.getParentTable(rootMenu));
        assertEquals("{\"menu in root\"}", RadioCommands.getParentTable(menu));
        assertEquals("{\"menu in root\", \"menu\"}", RadioCommands.getParentTable(subMenu));
    }

    @Test
    void getPathTable() {
        assertEquals("{\"menu in root\"}", RadioCommands.getPathTable(rootMenu));
        assertEquals("{\"menu in root\", \"menu\"}", RadioCommands.getPathTable(menu));
        assertEquals("{\"menu in root\", \"menu\", \"submenu\"}", RadioCommands.getPathTable(subMenu));
    }
}