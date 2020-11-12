package moe.ofs.backend.services.mybtais;

import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.ExportObject;
import moe.ofs.backend.mybatis.dao.ExportObjectDao;
import moe.ofs.backend.mybatis.service.ExportObjectService;
import moe.ofs.backend.object.Vector3D;
import moe.ofs.backend.object.map.GeoPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class AsyncCollectionJob {
    @Autowired
    private ExportObjectDao exportObjectDao;
    @Autowired
    private ExportObjectService exportObjectService;

    @Async
    public void collectDataToDB(Map<Long, ExportObject> newMap, Map<Long, ExportObject> oldMap) {
        log.info("******开始上传物体");
        Set<Long> newRuntimeIds = null;
        Set<Long> oldRuntimeIds = null;
//        数据未加入
        if (newMap.isEmpty() && oldMap.isEmpty()) {
            return;
        }

        if (!newMap.isEmpty()) {
            newRuntimeIds = newMap.keySet();
        }

        if (!oldMap.isEmpty()) {
            oldRuntimeIds = oldMap.keySet();
        }

//        新数据为空，旧数据不为空，删除上一次更新的数据
        if (newMap.isEmpty()) {
            exportObjectService.removeByIds(oldRuntimeIds);

            return;
        }


        if (oldMap.isEmpty()) {
            List<moe.ofs.backend.mybatis.entity.ExportObject> objectList = ExObjectListToDBObjectList(newMap);
            exportObjectService.saveOrUpdateBatch(objectList, objectList.size());

            return;
        }


//        取交集
        newRuntimeIds.retainAll(oldRuntimeIds);
//
        List<moe.ofs.backend.mybatis.entity.ExportObject> objectList = ExObjectListToDBObjectList(newMap);
        exportObjectService.saveOrUpdateBatch(objectList, objectList.size());

        if (newRuntimeIds.size() > 0) {
            oldRuntimeIds.removeAll(newRuntimeIds);
        }

        exportObjectService.removeByIds(oldRuntimeIds);

    }

    private List<moe.ofs.backend.mybatis.entity.ExportObject> ExObjectListToDBObjectList(Map<Long, ExportObject> newMap) {
        List<moe.ofs.backend.mybatis.entity.ExportObject> objectList = new ArrayList<>();

        Set<Long> longs = newMap.keySet();

        for (Long key : longs) {
            ExportObject v = newMap.get(key);
            GeoPosition geoPosition = v.getGeoPosition();
            Vector3D position = v.getPosition();

            moe.ofs.backend.mybatis.entity.ExportObject exportObject = new moe.ofs.backend.mybatis.entity.ExportObject(
                    v.getRuntimeID(), v.getBank(), v.getCoalition(), v.getCoalitionID(), v.getCountry(), v.getGroupName(), v.getHeading(),
                    v.getName(), v.getPitch(), v.getUnitName(), geoPosition.getAltitude(), geoPosition.getLatitude()
                    , geoPosition.getLongitude(), position.getX(), position.getY(), position.getZ());
            objectList.add(exportObject);
        }

        return objectList;
    }
}
