package com.wanglei.mybibackend.mq;

import com.rabbitmq.client.Channel;
import com.wanglei.mybibackend.constant.CommonConstant;
import com.wanglei.mybibackend.manage.AiManager;
import com.wanglei.mybibackend.model.domain.Chart;
import com.wanglei.mybibackend.model.enums.ChartStatus;
import com.wanglei.mybibackend.service.ChartService;
import com.wanglei.mybibackend.util.ExcelUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Component
@Slf4j
public class BiMessageConsumer {
    @Resource
    private AiManager aiManager;

    @Resource
    private ChartService chartService;

    @RabbitListener(queues = {MQConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("收到消息: " + message);
        if (StringUtils.isBlank(message)) {
            channel.basicNack(deliveryTag, false, false);
        }

        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);

        // 先保存到数据库，设置初始状态
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus(ChartStatus.RUNNING.getValue());
        boolean updated = chartService.updateById(updateChart);
        if (!updated) {
            chartService.handleChartUpdateError(chart.getId(), "更新图表失败");
            return;
        }
        //调用 AI
        String result = aiManager.doChat(CommonConstant.BI_MODEL_ID, getUserInput(chart));
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            chartService.handleChartUpdateError(chart.getId(), "AI 生成错误");
            return;
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        // 插入到数据库
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        updateChartResult.setStatus(ChartStatus.SUCCESS.getValue());
        boolean updateSaveResult = chartService.updateById(updateChartResult);
        if (!updateSaveResult) {
            chartService.handleChartUpdateError(chart.getId(), "更新图表失败");
        }

        //消息确认
        channel.basicAck(deliveryTag, false);

    }

    private String getUserInput(Chart chart) {
        String csvData = chart.getChartData();
        String chartType = chart.getChartType();
        String goal = chart.getGoal();
        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        // 压缩后的数据
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }
}
