package moe.ofs.backend.chatcmdnew.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"name", "keyword"})
public class ChatCommandDefinition {

    @NonNull
    private String name;

    @NonNull
    private String keyword;

    private String description;

    @NonNull
    @Builder.Default
    private List<String> affectedPlayerUcidList = new ArrayList<>();

    @NonNull
    private Consumer<ChatCommandProcessEntity> consumer;

    private ScanStrategy strategy;


    private ChatCommandDefinition(String name, String keyword, String description,
                                  List<String> affectedPlayerUcidList,
                                 Consumer<ChatCommandProcessEntity> consumer, ScanStrategy strategy) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name of a ChatCommandDefinition cannot be empty String");
        }

        if (keyword.isEmpty()) {
            throw new IllegalArgumentException("Keyword of a ChatCommandDefinition cannot be empty String");
        }

        this.name = name;
        this.keyword = keyword;
        this.description = description;
        this.affectedPlayerUcidList = affectedPlayerUcidList;
        this.consumer = consumer;
        this.strategy = strategy;
    }
}
