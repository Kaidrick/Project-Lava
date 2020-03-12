package moe.ofs.backend.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides methods to read write and delete to Saved Games/DCS folder
 */
public class DcsScriptConfigManager {

    private static final Path TEST_DCS_VARIANT_PATH = Paths.get("DCS.openbeta_server");
    private static final Path SAVED_GAMES_PATH = Paths.get(System.getProperty("user.home")).resolve("Saved Games");

    private static final Path HOOK_PATH = Paths.get("Scripts/Looks");
    private static final Path TARGET_HOOK = Paths.get("ofsmizzzzz.lua");

    private static final Path TARGET_EXPORT = Paths.get("Scripts/Fxport.lua");
    private static final Path TARGET_LAVA = Paths.get("Scripts/DCS-Kava.lua");

    public void checkUserDcsWritePaths() throws IOException {
        Files.walk(SAVED_GAMES_PATH, 1)
                .filter(p -> p.getFileName().toString().contains("DCS"))

                // populate gui drop menu
                .forEach(System.out::println);
    }

    public void injectIntoHooks(Path aWritePath) throws IOException {
        // check for Scripts/Hooks folder
        Path hooksPath = SAVED_GAMES_PATH.resolve(aWritePath).resolve(HOOK_PATH);
        Path target = Files.createDirectories(hooksPath).resolve(TARGET_HOOK);

        try(InputStream in = LuaScripts.class.getClassLoader().getResourceAsStream("scripts/inject/ofsmiz.lua")) {
            if(in != null) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);

                System.out.println(target);
            }
        }
    }

    public boolean injectionConfigured(Path path) {
        // check for existence of
        return false;
    }

    public void backupOriginal(Path path) throws IOException {
        Path backupTarget = path.resolveSibling(path.getFileName() + ".bkp");
        Files.copy(path, backupTarget);
    }

    public void removeInjection(Path path) throws IOException {
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

    public void injectIntoExport(Path aWritePath) throws IOException {
        // check if Export.lua exists
        Path exportTarget = SAVED_GAMES_PATH.resolve(aWritePath).resolve(TARGET_EXPORT);
        Path lavaTarget = SAVED_GAMES_PATH.resolve(aWritePath).resolve(TARGET_LAVA);

        // copy DCS-Lava.lua anyway
        try(InputStream in = LuaScripts.class.getClassLoader().getResourceAsStream("scripts/inject/DCS-Lava.lua")) {
            if(in != null) {
                Files.copy(in, lavaTarget, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        if(Files.exists(exportTarget)) {
            // modify Export.lua if declaration does not exist
            String injectDeclaration = LuaScripts.load("inject/lava_decl.lua");
            String exportContent = String.join("\n", Files.readAllLines(exportTarget));

            if(!exportContent.contains("DCS-Lava.lua")) {  // does contain this declaration, do nothing
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

    public static void main(String[] args) throws IOException {
        DcsScriptConfigManager manager = new DcsScriptConfigManager();
//        manager.injectIntoHooks(TEST_DCS_VARIANT_PATH);

//        manager.injectIntoExport(TEST_DCS_VARIANT_PATH);

        manager.removeInjection(TEST_DCS_VARIANT_PATH);
    }
}
