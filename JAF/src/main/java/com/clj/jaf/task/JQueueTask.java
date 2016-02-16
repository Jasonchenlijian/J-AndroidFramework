package com.clj.jaf.task;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class JQueueTask {

    private Queue<JTask> mTTasks = new LinkedList();

    public JQueueTask() {
    }

    public Queue<JTask> getTasks() {
        return this.mTTasks;
    }

    public boolean addTask(JTask task) {
        JTask innerTask = this.hasTask(task.getTask().getTaskId());
        return innerTask != null?false:this.mTTasks.add(task);
    }

    public boolean delTask(int taskId) {
        JTask task = this.hasTask(taskId);
        return task == null?false:this.mTTasks.remove(task);
    }

    public boolean delTask(JTask task) {
        return this.mTTasks.remove(task);
    }

    public JTask hasTask(int taskId) {
        Iterator var3 = this.mTTasks.iterator();

        while(var3.hasNext()) {
            JTask task = (JTask)var3.next();
            if(task.getTask().getTaskId() == taskId) {
                return task;
            }
        }

        return null;
    }

    public void clearTask() {
        JTask task;
        for(Iterator var2 = this.mTTasks.iterator(); var2.hasNext(); task = null) {
            task = (JTask)var2.next();
            task.stopTask();
        }

        this.mTTasks.clear();
    }
}
