package moe.ofs.backend.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class GraveyardRecord extends BaseEntity {
    private final ExportObject record;

    private final Instant expirationTime;
    private final Instant createTime;
}
