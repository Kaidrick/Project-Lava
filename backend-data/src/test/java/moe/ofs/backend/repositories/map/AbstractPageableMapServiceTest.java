package moe.ofs.backend.repositories.map;

import moe.ofs.backend.common.AbstractPageableMapService;
import moe.ofs.backend.domain.dcs.poll.PlayerInfo;
import moe.ofs.backend.domain.pagination.PageObject;
import moe.ofs.backend.domain.pagination.PageVo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractPageableMapServiceTest {

    AbstractPageableMapService<PlayerInfo> service = new AbstractPageableMapService<PlayerInfo>() {};

    @BeforeEach
    void setUp() {
        StringBuilder stringBuilder = new StringBuilder("Player ");

        for (int i = 0; i < 12; i++) {
            PlayerInfo playerInfo = new PlayerInfo();
            playerInfo.setPing(0);
            playerInfo.setSide(1);
            playerInfo.setStarted(true);
            playerInfo.setNetId(i + 1);
            playerInfo.setUcid(String.valueOf(i));
            playerInfo.setName(stringBuilder.replace(stringBuilder.length() - 1,
                    stringBuilder.length(), String.valueOf(i)).toString());

            service.save(playerInfo);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void deleteById() {

    }

    @Test
    void save() {
        PageObject pageObject = new PageObject();
        pageObject.setCurrentPageNo(2L);
        pageObject.setPageSize(10);

        PageVo<PlayerInfo> result = service.findPage(pageObject);
        Assertions.assertEquals(2, result.getData().size());

        // add a new player info object
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setPing(0);
        playerInfo.setSide(1);
        playerInfo.setStarted(true);
        playerInfo.setNetId(666);
        playerInfo.setUcid(String.valueOf(666));
        playerInfo.setName("Player 666");

        service.save(playerInfo);

        PageVo<PlayerInfo> resultAfterSave = service.findPage(pageObject);
        Assertions.assertEquals(3, resultAfterSave.getData().size());
        Assertions.assertEquals("Player 666", resultAfterSave.getData().get(2).getName());

        service.delete(playerInfo);
    }

    @Test
    void deleteAll() {
    }

    @Test
    void delete() {

    }

    @Test
    void findPage() {
        PageObject pageObject = new PageObject();
        pageObject.setCurrentPageNo(2L);
        pageObject.setPageSize(10);

        PageVo<PlayerInfo> result = service.findPage(pageObject);
        Assertions.assertEquals(2, result.getData().size());

        pageObject.setCurrentPageNo(1L);
        pageObject.setPageSize(10);

        PageVo<PlayerInfo> resultPageOne = service.findPage(pageObject);
        Assertions.assertEquals(10, resultPageOne.getData().size());

        pageObject.setCurrentPageNo(1L);
        pageObject.setPageSize(20);

        PageVo<PlayerInfo> resultTwenty = service.findPage(pageObject);
        Assertions.assertEquals(12, resultTwenty.getData().size());

        pageObject.setCurrentPageNo(3L);
        pageObject.setPageSize(20);

        PageVo<PlayerInfo> resultOutOfBound = service.findPage(pageObject);
        Assertions.assertEquals(0, resultOutOfBound.getData().size());

    }
}