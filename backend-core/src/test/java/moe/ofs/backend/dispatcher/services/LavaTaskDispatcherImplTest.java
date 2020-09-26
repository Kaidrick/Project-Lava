package moe.ofs.backend.dispatcher.services;

import moe.ofs.backend.dispatcher.model.LavaTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LavaTaskDispatcherImplTest {

    private TaskController controller;
    private LavaTaskDispatcher dispatcher;


    @BeforeEach
    void setUp() {
        controller = new TaskControllerImpl();
        dispatcher = new LavaTaskDispatcherImpl(controller);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addTask() {

        dispatcher.init();
    }

    @Test
    void removeTaskByName() {
    }
}