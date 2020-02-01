import core.box.BoxOfFlyableUnit;
import core.request.BaseRequest;
import core.request.ExportPollingHandler;
import core.request.RequestHandler;
import core.request.ServerPollingHandler;
import core.request.server.ServerLuaMemoryDataRequest;
import core.request.server.handler.PlayerEnterServerObservable;
import core.request.server.handler.PlayerLeaveServerObservable;
import core.request.server.handler.PlayerSlotChangeObservable;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;


public class BackendMainController {

    private static final RequestHandler<BaseRequest> requestHandler = RequestHandler.getInstance();
////    private static MissionPollingHandler missionPollingHanlder = MissionPollingHandler.getInstance();
    private static final ExportPollingHandler exportPollingHandler = ExportPollingHandler.getInstance();
    private static final ServerPollingHandler serverPollingHandler = ServerPollingHandler.getInstance();

    public static void main(String[] args) throws IOException {

//        BoxOfFlyableUnit.init();

        PlayerEnterServerObservable playerEnterServerObservable =
                playerInfo -> System.out.println("New connection: " + playerInfo.getName()
                        + "@" + playerInfo.getIpaddr());
        playerEnterServerObservable.register();

        PlayerLeaveServerObservable playerLeaveServerObservable =
                playerInfo -> System.out.println("player left: " + playerInfo.getName()
                        + "@" + playerInfo.getIpaddr());
        playerLeaveServerObservable.register();

        PlayerSlotChangeObservable playerSlotChangeObservable =
                (previous, current) -> System.out.println(
                        current.getName()
                                + " slot change: " + previous.getSlot() + " -> " + current.getSlot());
        playerSlotChangeObservable.register();

//        Runnable polling = () -> {
////            BaseRequest.getFillerInstance().send();
//            try {
////                pollingHandler.poll(pollingHandler);
//                missionPollingHanlder.poll();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        };

        Runnable exportPolling = () -> {
            try {
                exportPollingHandler.poll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Runnable serverPolling = () -> {
          try {
              serverPollingHandler.poll();
//              throw new IOException();
          } catch (IOException e) {
              e.printStackTrace();
          }
        };

//        new DebugRequest("trigger.action.outText('what?', 5)", true).send();

        // main loop
//         requests are sent and result are received in this thread only
        Runnable mainLoop = () -> {
//                BaseRequest.getFillerInstance().send();
            try {
                requestHandler.transmitAndReceive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

//         dedicated polling thread
//         polling and receive data only in this thread
        ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
        es.scheduleWithFixedDelay(mainLoop, 1000, 1, TimeUnit.MILLISECONDS);

//        ScheduledExecutorService exportPollingScheduler = Executors.newSingleThreadScheduledExecutor();
//        exportPollingScheduler.scheduleWithFixedDelay(exportPolling,
//                1000, 1, TimeUnit.MILLISECONDS);


        ScheduledExecutorService serverPollingScheduler = Executors.newSingleThreadScheduledExecutor();
        serverPollingScheduler.scheduleWithFixedDelay(serverPolling,
                1000, 1, TimeUnit.MILLISECONDS);


//        ScheduledExecutorService ep = Executors.newSingleThreadScheduledExecutor();
//        ep.scheduleWithFixedDelay(() -> new ServerExecRequest("return timer.getAbsTime()").send()
//                , 4000, 1000, TimeUnit.MILLISECONDS);

//        ep.scheduleWithFixedDelay(() -> new ServerLuaMemoryDataRequest().send()
//                , 2000, 1000, TimeUnit.MILLISECONDS);
//
    }
}
