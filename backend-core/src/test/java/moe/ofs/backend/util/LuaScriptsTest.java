package moe.ofs.backend.util;

import moe.ofs.backend.connector.util.LuaScripts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LuaScriptsTest {

    String SCRIPT_NAME = "message/send_message_by_group_id.lua";
    int GROUP_ID = 737;
    String CONTENT = "test";
    int DURATION = 10;
    boolean CLEAR_VIEW = true;
    boolean NO_CLEAR_VIEW = false;

    @Test
    void loadAndPrepare() {
        String string1 = LuaScripts.loadAndPrepare(SCRIPT_NAME, GROUP_ID, CONTENT, DURATION, NO_CLEAR_VIEW);
        assertEquals("trigger.action.outTextForGroup(737, 'test', 10, false)", string1);

        String string2 = LuaScripts.loadAndPrepare(SCRIPT_NAME, GROUP_ID, CONTENT, DURATION, CLEAR_VIEW);
        assertEquals("trigger.action.outTextForGroup(737, 'test', 10, true)", string2);
    }
}