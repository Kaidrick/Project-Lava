package moe.ofs.backend.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import moe.ofs.backend.domain.Level;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides methods to read write and delete to Saved Games/DCS folder
 */

@Slf4j
public class DcsScriptConfigManager {
    public static boolean objectDeltaValueMode;

    public static final Path TEST_DCS_VARIANT_PATH = Paths.get("DCS.openbeta_server");
    public static final Path SAVED_GAMES_PATH = Paths.get(System.getProperty("user.home")).resolve("Saved Games");
    public static final Path LAVA_DATA_PATH = SAVED_GAMES_PATH.resolve("Lava");

    private static final Path HOOK_PATH = Paths.get("Scripts/Hooks");
    private static final Path TARGET_HOOK = Paths.get("ofsmiz.lua");

    private static final Path TARGET_EXPORT = Paths.get("Scripts/Export.lua");
    private static final Path TARGET_LAVA = Paths.get("Scripts/DCS-Lava.lua");

    @SneakyThrows
    public ObservableList<Path> getUserDcsWritePaths() {
        ObservableList<Path> list = FXCollections.observableArrayList();

        Files.walk(SAVED_GAMES_PATH, 1)
                .filter(p -> p.getFileName().toString().startsWith("DCS.") || p.getFileName().toString().equals("DCS"))
                // for populating gui drop menu
                .forEach(list::add);

        return list;
    }

    /**
     * Injects Hooks script into DCS write path, replacing default ports with overridden ports if an xml config exists.
     * @param aWritePath branch name of dcs world
     */
    @SneakyThrows
    public void injectIntoHooks(Path aWritePath) {
        // check for Scripts/Hooks folder
        Path hooksPath = SAVED_GAMES_PATH.resolve(aWritePath).resolve(HOOK_PATH);
        Path target = Files.createDirectories(hooksPath).resolve(TARGET_HOOK);

        try(InputStream in = LuaScripts.class.getClassLoader().getResourceAsStream("scripts/inject/delta/ofsmiz.lua")) {
            if(in != null) {

                Integer overriddenDataPort =
                        ConnectionManager.getInstance().getPortOverrideMap().get(Level.SERVER_POLL);
                Integer overriddenQueryPort =
                        ConnectionManager.getInstance().getPortOverrideMap().get(Level.SERVER);

                String content = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines()
                        .collect(Collectors.joining("\n"));

                String preparedContent;
                int dataPort = overriddenDataPort != null ? overriddenDataPort : Level.SERVER_POLL.getPort();
                int queryPort = overriddenQueryPort != null ? overriddenQueryPort : Level.SERVER.getPort();

                preparedContent = String.format(content,
                        queryPort, dataPort);
                log.info("Injecting hook scripts into " + target + ": query and data port -> " +
                        queryPort + ", " + dataPort);
                Files.write(target, preparedContent.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    @SneakyThrows
    public void injectIntoExport(Path aWritePath) {
        // check if Export.lua exists
        Path exportTarget = SAVED_GAMES_PATH.resolve(aWritePath).resolve(TARGET_EXPORT);
        Path lavaTarget = SAVED_GAMES_PATH.resolve(aWritePath).resolve(TARGET_LAVA);

        // copy DCS-Lava.lua anyway
        try(InputStream in = LuaScripts.class.getClassLoader().getResourceAsStream("scripts/inject/delta/DCS-Lava.lua")) {
            if(in != null) {
                Integer overriddenDataPort =
                        ConnectionManager.getInstance().getPortOverrideMap().get(Level.EXPORT_POLL);
                Integer overriddenQueryPort =
                        ConnectionManager.getInstance().getPortOverrideMap().get(Level.EXPORT);

                String content = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines()
                        .collect(Collectors.joining("\n"));

                String preparedContent;
                int dataPort = overriddenDataPort != null ? overriddenDataPort : Level.EXPORT_POLL.getPort();
                int queryPort = overriddenQueryPort != null ? overriddenQueryPort : Level.EXPORT.getPort();

                preparedContent = String.format(content,
                        queryPort, dataPort);
                log.info("Injecting export scripts into " + lavaTarget + ": query and data port -> " +
                        queryPort + ", " + dataPort);
                Files.write(lavaTarget, preparedContent.getBytes(StandardCharsets.UTF_8));
            }
        }

        if(Files.exists(exportTarget)) {
            // modify Export.lua if declaration does not exist
            String injectDeclaration = LuaScripts.load("inject/lava_decl.lua");
            String exportContent = String.join("\n", Files.readAllLines(exportTarget));

            if(!exportContent.contains("DCS-Lava.lua")) {  // does not contain this declaration
                BufferedWriter writer = Files.newBufferedWriter(exportTarget, StandardCharsets.UTF_8);
                writer.append(exportContent).append("\n\n").append(injectDeclaration);
                writer.close();
            } else {  // if content contains this, remove lines and place them to the end of file

            }
        } else {
            // create a file named Export.lua and append a line to file
            String injectDeclaration = LuaScripts.load("inject/lava_decl.lua");
            System.out.println("injectDeclaration = " + injectDeclaration);
            BufferedWriter writer = Files.newBufferedWriter(exportTarget, StandardCharsets.UTF_8);
            writer.write(injectDeclaration + "\n");
            writer.close();
        }
    }

    @SneakyThrows
    public boolean isInjectionConfigured(Path path) {
        // check for existence of Export.lua entry and hooks
        Path hook = SAVED_GAMES_PATH.resolve(path).resolve(HOOK_PATH).resolve(TARGET_HOOK);
        Path lava = SAVED_GAMES_PATH.resolve(path).resolve(TARGET_LAVA);
        Path export = SAVED_GAMES_PATH.resolve(path).resolve(TARGET_EXPORT);

        String hookRef = LuaScripts.load("inject/delta/ofsmiz.lua");
        String lavaRef = LuaScripts.load("inject/delta/DCS-Lava.lua");
        String exportRef = LuaScripts.load("inject/lava_decl.lua");

        Integer serverOverriddenDataPort =
                ConnectionManager.getInstance().getPortOverrideMap().get(Level.SERVER_POLL);
        Integer serverOverriddenQueryPort =
                ConnectionManager.getInstance().getPortOverrideMap().get(Level.SERVER);
        Integer exportOverriddenDataPort =
                ConnectionManager.getInstance().getPortOverrideMap().get(Level.EXPORT_POLL);
        Integer exportOverriddenQueryPort =
                ConnectionManager.getInstance().getPortOverrideMap().get(Level.EXPORT);

        int exportDataPort = exportOverriddenDataPort != null ? exportOverriddenDataPort : Level.EXPORT_POLL.getPort();
        int exportQueryPort = exportOverriddenQueryPort != null ? exportOverriddenQueryPort : Level.EXPORT.getPort();
        int serverDataPort = serverOverriddenDataPort != null ? serverOverriddenDataPort : Level.SERVER_POLL.getPort();
        int serverQueryPort = serverOverriddenQueryPort != null ? serverOverriddenQueryPort : Level.SERVER.getPort();

        String hookRefOverriddenPort = String.format(hookRef, serverQueryPort, serverDataPort);
        String lavaRefOverriddenPort = String.format(lavaRef, exportQueryPort, exportDataPort);

        if(Files.exists(export) && Files.exists(lava) && Files.exists(hook)) {
            String hookContent = String.join("\n", Files.readAllLines(hook));
            String lavaContent = String.join("\n", Files.readAllLines(lava));
            String exportContent = String.join("\n", Files.readAllLines(export));

            return hookContent.equals(hookRefOverriddenPort)
                    && lavaContent.equals(lavaRefOverriddenPort)
                    && exportContent.contains(exportRef);

        } else {
            return false;
        }
    }

    @SneakyThrows
    public void backupOriginal(Path path) {
        Path backupTarget = path.resolveSibling(path.getFileName() + ".bkp");
        Files.copy(path, backupTarget);
    }

    @SneakyThrows
    public void removeInjection(Path path) {
        Path exportTarget = SAVED_GAMES_PATH.resolve(path).resolve(TARGET_EXPORT);

        List<String> lines = Files.readAllLines(exportTarget);
        String[] declLines = LuaScripts.load("inject/lava_decl.lua").split("\\r?\\n");

        List<String> cleanedContent = new ArrayList<>(lines);
        for (String s : declLines) {
            cleanedContent = cleanedContent.stream().filter(l -> !l.contains(s)).collect(Collectors.toList());
        }

        BufferedWriter writer = Files.newBufferedWriter(exportTarget, StandardCharsets.UTF_8);
        writer.write(String.join("\n", cleanedContent));
        writer.close();

        Path hook = SAVED_GAMES_PATH.resolve(path).resolve(HOOK_PATH).resolve(TARGET_HOOK);
        Path lava = SAVED_GAMES_PATH.resolve(path).resolve(TARGET_LAVA);
        if(Files.exists(hook))
            Files.delete(SAVED_GAMES_PATH.resolve(path).resolve(HOOK_PATH).resolve(TARGET_HOOK));
        if(Files.exists(lava))
            Files.delete(SAVED_GAMES_PATH.resolve(path).resolve(TARGET_LAVA));
    }
}
