package moe.ofs.backend.gui;

import javafx.scene.control.ListView;
import moe.ofs.backend.repositories.PlayerInfoRepository;
import moe.ofs.backend.services.FlyableUnitService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class PlayerListCellFactory implements FactoryBean<PlayerListCell> {

    private PlayerInfoRepository playerInfoRepository;

    private FlyableUnitService flyableUnitService;

    private ListView<String> listView;

    public PlayerListCellFactory(PlayerInfoRepository playerInfoRepository, FlyableUnitService flyableUnitService) {
        this.playerInfoRepository = playerInfoRepository;
        this.flyableUnitService = flyableUnitService;
    }

    public PlayerListCellFactory listView(ListView<String> listView) {
        this.listView = listView;

        return this;
    }

    @Override
    public PlayerListCell getObject() {
        return new PlayerListCell(listView, playerInfoRepository, flyableUnitService);
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
