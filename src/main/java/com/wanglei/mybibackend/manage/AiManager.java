package com.wanglei.mybibackend.manage;

import com.wanglei.mybibackend.commmon.ErrorCode;
import com.wanglei.mybibackend.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 用于对接 AI 平台
 */
@Service
public class AiManager {


    /**
     * AI 对话
     *
     * @param modelId
     * @param message
     * @return
     */
    public String doChat(long modelId, String message) {
        String accessKey = "7ura61tu6845y61nqbq5ggb3naihjzu0";
        String secretKey = "en2z887d2zj1fpyreewedyi5afb6qtuk";
        YuCongMingClient yuCongMingClient = new YuCongMingClient(accessKey, secretKey);
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);
        if (response == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应错误");
        }
        return response.getData().getContent();
    }
}
