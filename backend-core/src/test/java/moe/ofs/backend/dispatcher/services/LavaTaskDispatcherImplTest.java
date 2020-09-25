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

    private final LavaTask task1 = LavaTask.builder()
            .task(() -> System.out.println("test task 1"))
            .interval(1000)
            .isPeriodic(true)
            .name("test task name 1")
            .source(this.getClass()).build();

    private final LavaTask task2 = LavaTask.builder()
            .task(() -> System.out.println("test task 2"))
            .interval(10000)
            .isPeriodic(true)
            .name("test task name 2")
            .source(this.getClass()).build();

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
        dispatcher.addTask(task1);
        dispatcher.addTask(task2);


        dispatcher.init();
    }

    @Test
    void removeTaskByName() {
    }
}