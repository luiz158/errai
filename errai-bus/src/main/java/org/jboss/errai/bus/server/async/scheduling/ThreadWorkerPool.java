/*
 * Copyright 2009 JBoss, a divison Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.bus.server.async.scheduling;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ThreadWorkerPool {
    /**
     * The worker pool.
     */
    private final List<ThreadWorker> workers;
    private final TaskProvider provider;

    private double lastLoad;
    private double maximumLoad = 0.40d;
    private int maximumPoolSize = 100;

    private volatile boolean stop = false;

    public ThreadWorkerPool(TaskProvider provider) {
        this.workers = new CopyOnWriteArrayList<ThreadWorker>();
        this.provider = provider;
    }

    public ThreadWorkerPool(TaskProvider provider, int maximumPoolSize) {
        this.workers = new CopyOnWriteArrayList<ThreadWorker>();
        this.provider = provider;
        this.maximumPoolSize = maximumPoolSize;
    }


    public ThreadWorkerPool(TaskProvider provider, double maximumLoad) {
        this.workers = new CopyOnWriteArrayList<ThreadWorker>();
        this.provider = provider;
        this.maximumLoad = maximumLoad;
    }

    public ThreadWorkerPool(TaskProvider provider, double maximumLoad, int maximumPoolSize) {
        this.workers = new CopyOnWriteArrayList<ThreadWorker>();
        this.provider = provider;
        this.maximumLoad = maximumLoad;
        this.maximumPoolSize = maximumPoolSize;
    }

    public void checkLoad() {
        double load = getApparentLoad();

     //   System.out.println("[ThreadWorkerPool; Load:" + load + "; TotalThreads:" + workers.size() + "]");

        if (load > maximumLoad)
            addWorker();

        if (lastLoad != 0 && (lastLoad - load) > 0.1d)
            removeWorker();

        lastLoad = load;
    }

    public double getApparentLoad() {
        double load = 0;

        // we count how many we iterate as workers.size() will be unrealiable.
        int count = 0;
        for (ThreadWorker worker : workers) {
            count++;
            load += worker.getApparentLoad();
        }
        return load / count;
    }

    public void addWorker() {
        synchronized (this) {
            if (workers.size() == maximumPoolSize) return;

            if (stop) {
                return;
            }

            ThreadWorker worker = new ThreadWorker(provider);
            workers.add(worker);
            worker.start();
        }
    }

    public void removeWorker() {
        synchronized (this) {
            if (workers.size() <= 1) return;

            if (stop) {
                return;
            }

            ThreadWorker worker = workers.get(workers.size() - 1);
            worker.requestStop();
            workers.remove(worker);
        }
    }

    public void startPool() {
        addWorker();
    }

    public void requestStopAll() {
        synchronized (this) {
            stop = true;

            for (ThreadWorker worker : workers)
                worker.requestStop();
        }
    }
}