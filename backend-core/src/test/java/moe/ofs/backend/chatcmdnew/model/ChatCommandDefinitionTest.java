package moe.ofs.backend.chatcmdnew.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class ChatCommandDefinitionTest {

    ChatCommandDefinition t1;
    ChatCommandDefinition t2;
    ChatCommandDefinition t3;
    ChatCommandDefinition t4;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void givenEmptyOrNullNameOrKeyWord_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(NullPointerException.class, () ->
                t1 = ChatCommandDefinition.builder().build());

        Assertions.assertThrows(NullPointerException.class, () ->
                t2 = ChatCommandDefinition.builder()
                        .name("test")
                        .keyword("/test")
                        .affectedPlayerUcidList(null)
                        .consumer(s -> {})
                        .build());

        Assertions.assertThrows(NullPointerException.class, () ->
                t3 = ChatCommandDefinition.builder()
                        .name("test")
                        .keyword("/test")
                        .affectedPlayerUcidList(new LinkedList<>())
                        .consumer(null)
                        .build());

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                t4 = ChatCommandDefinition.builder()
                        .name("")
                        .keyword("")
                        .consumer(s -> {})
                        .strategy(null)
                        .build());
    }
}