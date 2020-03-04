package com.juliuskrah.task;

import org.springframework.cloud.task.listener.TaskExecutionListener;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TheShopTaskExecutionListener implements TaskExecutionListener {

	@Override
	public void onTaskStartup(TaskExecution taskExecution) {
		log.info("Task has commerced");
	}

	@Override
	public void onTaskEnd(TaskExecution taskExecution) {
		log.info("Task has ended");
	}

	@Override
	public void onTaskFailed(TaskExecution taskExecution, Throwable throwable) {
		log.error("Task has failed");
	}

}
