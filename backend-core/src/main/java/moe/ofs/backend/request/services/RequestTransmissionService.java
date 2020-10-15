package moe.ofs.backend.request.services;

import moe.ofs.backend.request.BaseRequest;
import moe.ofs.backend.request.JsonRpcRequest;

public interface RequestTransmissionService {
    BaseRequest send(BaseRequest request);

    JsonRpcRequest toJsonRpcCall(BaseRequest request);

    void prepareParameters(BaseRequest request);

    String toMessageString(BaseRequest request);
}
