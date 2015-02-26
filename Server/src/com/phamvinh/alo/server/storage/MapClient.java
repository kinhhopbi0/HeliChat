/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.phamvinh.alo.server.storage;

import com.phamvinh.network.server.transport.Client;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author via
 */
public class MapClient {
    public static ConcurrentHashMap<String, Client> clients = 
            new ConcurrentHashMap<String, Client>(10000);
}
