package moe.ofs.backend.services;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.object.Vector3D;
import moe.ofs.backend.object.map.GeoPosition;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UpdatableServiceTest {

    @Mock
    private UpdatableService<ExportObject> exportObjectUpdatableService;

    private ExportObject record;

    private ExportObject update;

    private List<String> updateFieldExcluded;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        record = ExportObject.builder()
                .bank(0.2222)
                .coalition("blue")
                .coalitionID(2)
                .country(17)
                .geoPosition(new GeoPosition())
                .groupName("Test Group Name Unmodifiable")
                .heading(0.23345)
                .id(1L)
                .name("Test Unit Name")
                .pitch(1.235)
                .position(new Vector3D(0, 0, 0))
                .runtimeID(8000456)
                .status(new HashMap<>())
                .type(new HashMap<>())
                .unitName("Test Type Name")
                .build();

        // update fields: bank, coalition, geoPosition, groupName, heading, id, pitch, position, runtimeID
        update = ExportObject.builder()
                .bank(1.6722)
//                .coalition("blue")
                .coalitionID(2)
                .country(17)
                .geoPosition(new GeoPosition())
//                .groupName("Test Group Name Unmodifiable")
                .heading(0.23345)
                .id(1L)
                .name(null)
                .pitch(1.235)
                .position(new Vector3D(4, 5, -8))
                .runtimeID(8000456)
//                .status(new HashMap<>())
                .type(null)
                .unitName("Test Type Name")
                .build();

        updateFieldExcluded = Arrays.asList("coalition", "groupName", "status", "name", "type");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void fieldUpdate() {
        Mockito.when(exportObjectUpdatableService.fieldUpdate(record, update)).thenCallRealMethod();

        assertEquals(record.getRuntimeID(), update.getRuntimeID());
        assertTrue(updateFieldExcluded.stream().noneMatch(s ->
                exportObjectUpdatableService.fieldUpdate(record, update).contains(s)));

        List<Field> fieldList = Arrays.stream(record.getClass().getDeclaredFields())
                .filter(f -> !updateFieldExcluded.contains(f.getName()))
                .collect(Collectors.toList());

        // check all if each field in the updated field list are actually updated
        fieldList.forEach(field -> {
            try {
                field.setAccessible(true);

                assertEquals(field.get(update), field.get(record));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                fail("Comparison failed for " + field.getName());
            }
        });
    }
}