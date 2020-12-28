package moe.ofs.backend.discipline.service;

public interface GlobalConnectionBlockService {
    void block(String reason);

    void release();
}
