package moe.ofs.backend.controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import moe.ofs.backend.handlers.ControlPanelShutdownObservable;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.net.URL;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEditor implements Initializable {

    private static final String[] KEYWORDS = new String[] {
            "and", "break", "do", "else", "elseif",
            "end", "false", "for", "function", "if",
            "in", "local", "nil", "not", "or",
            "repeat", "return", "then", "true", "until", "while",

            "xpcall", "tostring", "print",
            "unpack", "require", "getfenv", "setmetatable", "next",
            "assert", "tonumber", "rawequal", "collectgarbage",
            "getmetatable", "module", "rawset", "pcall",
            "newproxy", "_G", "select", "gcinfo", "pairs",
            "rawget", "loadstring", "ipairs", "_VERSION", "dofile", "setfenv",
            "load", "error", "loadfile",

            "sub", "upper", "len", "gfind", "rep", "find", "match", "char", "dump", "gmatch",
            "reverse", "byte", "format", "gsub", "lower", "preload", "loadlib", "loaded",
            "loaders", "cpath", "config", "path", "seeall", "exit", "setlocale", "date",
            "getenv", "difftime", "remove", "time", "clock", "tmpname", "rename", "execute",
            "lines", "write", "close", "flush", "open", "output", "type", "read", "stderr",
            "stdin", "input", "stdout", "popen", "tmpfile", "log", "max", "acos", "huge",
            "ldexp", "pi", "cos", "tanh", "pow", "deg", "tan", "cosh", "sinh", "random", "randomseed",
            "frexp", "ceil", "floor", "rad", "abs", "sqrt", "modf", "asin", "min", "mod", "fmod", "log10",
            "atan2", "exp", "sin", "atan", "getupvalue", "sethook", "getmetatable",
            "gethook", "setmetatable", "setlocal", "traceback", "setfenv", "getinfo",
            "setupvalue", "getlocal", "getregistry", "getfenv", "setn", "insert", "getn",
            "foreachi", "maxn", "foreach", "concat", "sort", "remove", "resume", "yield",
            "status", "wrap", "create", "running"
    };

    private static final String[] STANDARD_LIBRARIES = new String[] {
            "string", "package", "os", "io", "math", "debug", "table", "coroutine"
    };

    private static final String[] METATABLE = new String[] {
            "__add", "__sub", "__mod", "__unm", "__concat", "__lt", "__index", "__call", "__gc", "__metatable",
            "__mul", "__div", "__pow", "__len", "__eq", "__le", "__newindex", "__tostring", "__mode"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String STDLIB_PATTERN = "\\b(" + String.join("|", STANDARD_LIBRARIES) + ")\\b";
    private static final String METATABLE_PATTERN = "\\b(" + String.join("|", METATABLE) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\\[(?<ls>=*)\\[(.|\\R)*?]\\k<ls>]" + "|" + "\\[\\=*\\[(.|\\R)*$" + "|" + "\\[{2,}(.|\\R)*?" + "|" + "\"([^\"\\\\]|\\\\.)*\"" + "|" + "\'([^\'\\\\]|\\\\.)*\'";
    private static final String COMMENT_PATTERN = "--\\[(?<lc>=*)\\[(.|\\R)*?--]\\k<lc>]" + "|" + "--[^\\n]*";
    private static final String OPERATOR_PATTERN = "\\+|\\-|\\*|\\/|\\%|\\#|\\^|\\~|\\<|\\>|\\<\\=|\\=\\>|\\=\\=|\\~\\=|\\=|\\:";
    private static final String NUMERIC_PATTERN = "[1-9][\\.\\d]*";


    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<OPERATOR>" + OPERATOR_PATTERN + ")"
                    + "|(?<STDLIB>" + STDLIB_PATTERN + ")"
                    + "|(?<METATABLE>" + METATABLE_PATTERN + ")"
                    + "|(?<NUMERIC>" + NUMERIC_PATTERN + ")"
    );

    private static final String sampleCode = String.join("\n", new String[] {
            "-- Syntax Highlights Example\n" +
            "\n" +
            "--[[\n" +
            "Project Lava is powered by\n" +
            "  .   ____          _            __ _ _\n" +
            " /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\\n" +
            "( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\\n" +
            " \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )\n" +
            "  '  |____| .__|_| |_|_| |_\\__, | / / / /\n" +
            " =========|_|==============|___/=/_/_/_/\n" +
            "--]]\n" +
            "\n" +
            "local mission_name = \"高加索 波斯湾 DCS可能不支持的字符( ఠൠఠ )ﾉ\"\n" +
            "\n" +
            "local string = [==[\n" +
            "This is a multi-line long string.\n" +
            "This is the second line of the string.\n" +
            "Notice the \\\\r\\\\n in the front and the end of the string.\n" +
            "]==]\n" +
            "\n" +
            "local test_function = function() \n" +
            "    local cfg = {\n" +
            "        [\"current\"] = 5.56,\n" +
            "        [\"require_pure_textures\"] = true,\n" +
            "        [\"missionList\"] = {\n" +
            "            [1] = \"C:\\\\Users\\\\Administrator\\\\Saved Games\\\\DCS.openbeta_server\\\\Missions\\\\new 422d Env Test.miz\",\n" +
            "        }, -- end of [\"missionList\"]\n" +
            "    } -- end of cfg\n" +
            "\n" +
            "    local paste = 3\n" +
            "    if(paste == 3 and 2 < 1) then\n" +
            "        local simple_math = math.floor(320 + 210 / math.pi * 24 % 2) ~= 42\n" +
            "    end\n" +
            "\n" +
            "    for k, v in pairs() do\n" +
            "        trigger.action.outText(\"test\", 5)\n" +
            "    end\n" +
            "end\n"
    });


    @FXML
    private AnchorPane topAnchorPane;

    private CodeArea codeArea;

    public String readEditorContent() {
        return codeArea.getContent().getText();
    }

    public void clearEditorContent() {
        codeArea.replaceText("");
    }

    private ExecutorService executor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        topAnchorPane.getStyleClass().clear();
        topAnchorPane.getStylesheets().clear();

        executor = Executors.newSingleThreadExecutor();
        codeArea = new CodeArea();

        codeArea.getStylesheets().clear();
        codeArea.getStylesheets().add("luaeditor.css");

        codeArea.getStyleClass().clear();
        codeArea.getStyleClass().addAll("overridden-styled-text-area", "overridden-code-area");

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        StackPane stackPane = new StackPane(new VirtualizedScrollPane<>(codeArea));
        topAnchorPane.getChildren().add(stackPane);

        AnchorPane.setTopAnchor(stackPane, 5.0);
        AnchorPane.setBottomAnchor(stackPane, 5.0);
        AnchorPane.setLeftAnchor(stackPane, 5.0);
        AnchorPane.setRightAnchor(stackPane, 5.0);

        String file = CodeEditor.class.getResource("/java-keywords.css").toExternalForm();
        topAnchorPane.getStylesheets().add(file);

        Subscription cleanupWhenDone = codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(codeArea.multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);

        ControlPanelShutdownObservable executorShutdown = () -> {
            cleanupWhenDone.unsubscribe();
            executor.shutdown();
        };
        executorShutdown.register();

        codeArea.replaceText(0, 0, sampleCode);

//        System.out.println("codeArea.getContent() = " + codeArea.getContent().getText());
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("COMMENT") != null ? "comment" :
                    matcher.group("OPERATOR") != null ? "operator" :
                    matcher.group("STDLIB") != null ? "standard_libraries" :
                    matcher.group("NUMERIC") != null ? "numeric" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
