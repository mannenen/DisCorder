/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discorder.event.handler;

import com.discorder.event.RecordEvent;

/**
 *
 * @author nathan
 */
public interface RecordEventHandler {
    public void onStart(RecordEvent e);
    public void onPause(RecordEvent e);
    public void onStop(RecordEvent e);
}
