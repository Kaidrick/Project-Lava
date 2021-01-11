package moe.ofs.backend.connector.services;

import moe.ofs.backend.connector.request.BaseRequest;
import moe.ofs.backend.connector.response.JsonRpcRequest;

public interface RequestTransmissionService {
    BaseRequest send(BaseRequest request);

    JsonRpcRequest toJsonRpcCall(BaseRequest request);

    void prepareParameters(BaseRequest request);

    String toMessageString(BaseRequest request);
}
