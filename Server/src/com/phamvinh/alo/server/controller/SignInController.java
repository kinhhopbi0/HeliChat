package com.phamvinh.alo.server.controller;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pdv.heli.message.detail.SignInMessage;
import com.pdv.heli.message.detail.LinearStringMessage;
import com.pdv.heli.message.detail.SignInMessage.Status;
import com.phamvinh.alo.server.base.BaseController;
import com.phamvinh.alo.server.common.RandomString;
import com.phamvinh.alo.server.constant.SessionKey;
import com.phamvinh.alo.server.db.DbAction;
import com.phamvinh.alo.server.main.HeliServerStart;
import com.phamvinh.alo.server.proccess.MessageIO;
import com.phamvinh.network.server.transport.Client;

public final class SignInController  extends BaseController{
	private static Logger LOG = LogManager.getLogger();
	
	
	@Override
	protected boolean isLoginRequired() {
		// TODO Auto-generated method stub
		return false;
	}

}
