package moe.ofs.backend.function;

import moe.ofs.backend.services.FlyableUnitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RadioItemManagerTest {

    static final int GROUP_ID = 739;
    static final String ROOT_MENU = "menu in root";
    static final String MENU = "menu";
    static final String SUBMENU = "submenu";
    static final String ANOTHER_ROOT_MENU = "another menu in root";

    @Mock
    FlyableUnitService flyableUnitService;

    @InjectMocks
    RadioItemManager manager;

    RadioItem rootMenu;
    RadioItem menu;
    RadioItem subMenu;
    RadioItem anotherRootMenu;

    @BeforeEach
    void setUp() {
        rootMenu = manager.newRadioControl(GROUP_ID, ROOT_MENU);
        menu = manager.newRadioControl(GROUP_ID, MENU, rootMenu);
        subMenu = manager.newRadioControl(GROUP_ID, SUBMENU, menu);

        anotherRootMenu = manager.newRadioControl(GROUP_ID, ANOTHER_ROOT_MENU, null);
    }

    @Test
    void getParentTable() {
        assertEquals("nil", manager.getParentTable(rootMenu));
        assertEquals("{\"menu in root\"}", manager.getParentTable(menu));
        assertEquals("{\"menu in root\", \"menu\"}", manager.getParentTable(subMenu));
    }

    @Test
    void getPathTable() {
        assertEquals("{\"menu in root\"}", manager.getPathTable(rootMenu));
        assertEquals("{\"menu in root\", \"menu\"}", manager.getPathTable(menu));
        assertEquals("{\"menu in root\", \"menu\", \"submenu\"}", manager.getPathTable(subMenu));
    }
}