package com.pdv.heli.processmessage;

import com.pdv.heli.R;
import com.pdv.heli.app.HeliApplication;
import com.pdv.heli.message.detail.SignUpMessage;

public class ProcessMsgInBackground {
	public static final String TAG = ProcessMsgInBackground.class
			.getSimpleName();

	public static void processSignUpMessage(SignUpMessage msg) {
		switch (msg.getStatus()) {
		case SignUpMessage.Status.CREATE_SUCCESS:
			HeliApplication.getInstance().showToastFromOtherThread(
					HeliApplication.getInstance().getString(
							R.string.sign_up_success));
			break;
		case SignUpMessage.Status.CREATE_FAIL:
			switch (msg.getFailType()) {
			case SignUpMessage.FailType.PHONE_EXIST:
				HeliApplication.getInstance().showToastFromOtherThread(
						HeliApplication.getInstance().getString(
								R.string.phone_number_readly_exist));
				break;
			case SignUpMessage.FailType.OTHER:
				HeliApplication.getInstance().showToastFromOtherThread(
						HeliApplication.getInstance().getString(
								R.string.sign_up_undefied_error));
				break;
			}
			break;
		default:
			break;
		}
	}
}
