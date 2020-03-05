package moe.ofs.backend.dataset;

import moe.ofs.backend.object.ExportObject;
import moe.ofs.backend.repositories.ExportObjectHashMapRepository;
import moe.ofs.backend.services.ExportObjectHashMapUpdateService;
import moe.ofs.backend.services.ExportObjectJpaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExportUnitDataSetTest {

    @InjectMocks
    ExportObjectHashMapRepository exportObjectHashMapRepository;

    ExportObjectHashMapUpdateService exportObjectHashMapUpdateService;

    @InjectMocks
    ExportObjectJpaService exportObjectJpaService;


    ExportUnitDataSet exportUnitDataSet;

    List<ExportObject> mockUpdateList;

    ExportObject exportObject1;
    ExportObject exportObject2;
    ExportObject exportObject3;

    ExportObject updatedExportObject1;

    Map<String, Double> position1;
    Map<String, Double> position2;

    Map<String, Boolean> flags1;
    Map<String, Boolean> flags2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        exportObjectHashMapUpdateService = new ExportObjectHashMapUpdateService(exportObjectHashMapRepository);
        exportUnitDataSet = new ExportUnitDataSet(exportObjectHashMapUpdateService, exportObjectJpaService);

        populateHashMap();
    }

    @Test
    void cycle() {

        // add two objects
        exportUnitDataSet.cycle(Arrays.asList(exportObject1, exportObject2));
        assertEquals(2, exportObjectHashMapUpdateService.findAll().size());

        // update one object and add one new object
        updateExportObject();
        exportUnitDataSet.cycle(Arrays.asList(updatedExportObject1, exportObject2, exportObject3));
        assertEquals(3, exportObjectHashMapUpdateService.findAll().size());

        // remove one object
        exportUnitDataSet.cycle(Arrays.asList(updatedExportObject1, exportObject3));
        assertEquals(2, exportObjectHashMapUpdateService.findAll().size());

        // pass an empty list
        exportUnitDataSet.cycle(Collections.emptyList());
        assertEquals(0, exportObjectHashMapUpdateService.findAll().size());
    }

    private void updateExportObject() {
        updatedExportObject1 = ExportObject.builder().bank(1.11).coalition("blue").coalitionID(2)
                .country(31)
                .groupName("test group name 1").heading(0.0).id(1L)
                .name("test type name 1").pitch(0.0).runtimeID(12243456)
                .unitName("test unit name 1")
                .position(position2)
                .flags(flags1)
                .build();
    }

    private void populateHashMap() {
        // set up flags;
        flags1 = Stream.of(new Object[][] {
                { "AI_ON", true },
                { "Born", 	true },
                { "Human", false },
                { "IRJamming", false },
                { "Invisible", false },
                { "Jamming", false },
                { "RadarActive", false },
                { "Static", false },
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (Boolean) data[1]));

        flags2 = Stream.of(new Object[][] {
                { "AI_ON", false },
                { "Born", 	true },
                { "Human", false },
                { "IRJamming", false },
                { "Invisible", true },
                { "Jamming", false },
                { "RadarActive", true },
                { "Static", false },
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (Boolean) data[1]));

        // set up positions
        position1 = Stream.of(new Object[][] {
                { "x", -337608.4375 },
                { "y", 	-54923.078125 },
                { "z", 1064.7748275653 }
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (Double) data[1]));

        position2 = Stream.of(new Object[][] {
                { "x", -337143.1875 },
                { "y", 1062.4134521484 },
                { "z", -56369.29296875 }
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (Double) data[1]));


        // build export objects
        exportObject1 = ExportObject.builder().bank(1.11).coalition("blue").coalitionID(2)
                .country(31)
                .groupName("test group name 1").heading(0.0).id(1L)
                .name("test type name 1").pitch(0.0).runtimeID(12243456)
                .unitName("test unit name 1")
                .position(position1)
                .flags(flags1)
                .build();


        exportObject2 = ExportObject.builder().bank(0.73834).coalition("red").coalitionID(1)
                .country(17)
                .groupName("test group name 2").heading(0.110).id(2L)
                .name("test type name 2").pitch(0.0).runtimeID(252346753)
                .unitName("test unit name 2")
                .position(position1)
                .flags(flags1)
                .build();


        exportObject3 = ExportObject.builder().bank(-1.2345783).coalition("blue").coalitionID(2)
                .country(1)
                .groupName("test group name 3").heading(0.0).id(3L)
                .name("test type name 3").pitch(0.0).runtimeID(64556784)
                .unitName("test unit name 3")
                .position(position1)
                .flags(flags1)
                .build();

    }
}