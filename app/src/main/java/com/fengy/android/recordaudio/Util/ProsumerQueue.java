package com.fengy.android.recordaudio.Util;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by admin on 2018/4/22.
 */

public class ProsumerQueue {
    private BlockingQueue<BufferChunk> sharedQueue = new LinkedBlockingQueue();
    private LinkedList<Object> list = new LinkedList();

    public ProsumerQueue() {
    }

    public void enqueue(BufferChunk buffChunk) {
        LinkedList var2 = this.list;
        LinkedList var3 = this.list;
        synchronized(this.list) {
            this.list.add(buffChunk);
            this.list.notify();
        }
    }

    public BufferChunk dequeue() {
        BufferChunk buffChunk = null;
        LinkedList var2 = this.list;
        LinkedList var3 = this.list;
        synchronized(this.list) {
            if(this.list.isEmpty()) {
                try {
                    this.list.wait();
                } catch (InterruptedException var6) {
                    var6.printStackTrace();
                }
            }

            buffChunk = (BufferChunk)this.list.remove();
            return buffChunk;
        }
    }

    public void clear() {
        LinkedList var1 = this.list;
        LinkedList var2 = this.list;
        synchronized(this.list) {
            this.list.clear();
        }
    }
}
