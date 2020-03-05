package moe.ofs.backend.repositories;

import moe.ofs.backend.object.ExportObject;
import moe.ofs.backend.services.ExportObjectHashMapUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExportObjectHashMapRepositoryTest {

    @InjectMocks
    ExportObjectHashMapRepository exportObjectHashMapRepository;

    ExportObjectHashMapUpdateService exportObjectHashMapUpdateService;

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

        populateHashMap();

    }

    @Test
    void findByUnitName() {
        Optional<ExportObject> optional = exportObjectHashMapRepository.findByUnitName(exportObject1.getUnitName());
        assertEquals(Optional.of(exportObject1), optional);
    }

    @Test
    void findById() {
        Optional<ExportObject> optional = exportObjectHashMapRepository.findById(exportObject1.getRuntimeID());
        assertEquals(Optional.of(exportObject1), optional);
    }

    @Test
    void saveAndDeleteTest() {

        // uninitialized instance updatedExportObject1
        assertNull(exportObjectHashMapUpdateService.save(updatedExportObject1));
        assertEquals(1, exportObjectHashMapRepository.findAll().size());

        // save the same instance multiple times
        exportObjectHashMapUpdateService.save(exportObject2);
        exportObjectHashMapUpdateService.save(exportObject2);

        exportObjectHashMapUpdateService.save(exportObject3);
        assertEquals(3, exportObjectHashMapRepository.findAll().size());

        exportObjectHashMapUpdateService.deleteByRuntimeId(exportObject1.getRuntimeID());
        assertEquals(2, exportObjectHashMapRepository.findAll().size());
    }

    @Test
    void addOrUpdateAll() {
        List<ExportObject> list = Arrays.asList(exportObject2, exportObject3);
        exportObjectHashMapUpdateService.addOrUpdateAll(list);

        assertEquals(3, exportObjectHashMapRepository.findAll().size());

        updatedExportObject1 = ExportObject.builder().bank(1.11).coalition("blue").coalitionID(2)
                .country(31)
                .groupName("test group name 1").heading(0.0).id(1L)
                .name("test type name 1").pitch(0.0).runtimeID(12243456)
                .unitName("test unit name 1")
                .position(position2)
                .flags(flags1)
                .build();

        // change position of export object
        Map<String, Double> refPosition = exportObject1.getPosition();
        Map<String, Double> newPosition = updatedExportObject1.getPosition();
        assertNotEquals(refPosition, newPosition);

        List<ExportObject> updatedList = Arrays.asList(updatedExportObject1, exportObject2, exportObject3);

        exportObjectHashMapUpdateService.addOrUpdateAll(updatedList);

        Optional<ExportObject> optionalExportObject =
                exportObjectHashMapUpdateService.findByRuntimeId(updatedExportObject1.getRuntimeID());

        assertTrue(optionalExportObject.isPresent());
        assertEquals(position2, optionalExportObject.get().getPosition());
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


        // save export object 1
        exportObjectHashMapRepository.save(exportObject1.getRuntimeID(), exportObject1);
    }
}