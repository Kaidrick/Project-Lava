package moe.ofs.backend.function;

import moe.ofs.backend.object.Vector3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MarkPanelManagerTest {

    @InjectMocks
    MarkPanelManager markPanelManager;

    @BeforeEach
    void setUp() {

    }

    @Test
    void create() {
    }

    @Test
    void testNextId() {
        MarkPanel panel1 = markPanelManager.create(123, "test", new Vector3D(1, 2, 3));
        MarkPanel panel2 = markPanelManager.create(666, "test213", new Vector3D(7, 277, 34));

        assertEquals(1, panel1.getIndex());
        assertEquals(2, panel2.getIndex());
    }
}