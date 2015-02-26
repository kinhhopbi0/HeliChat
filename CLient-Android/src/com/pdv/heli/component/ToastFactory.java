package com.pdv.heli.component;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

public class ToastFactory {	

	public static class Builder {

		private CharSequence title;
		private CharSequence content;
		private Type type = Type.INFO;

		public Builder() {
			
		}

		public Toast build(Context pContext) {
			LayoutInflater inflater = (LayoutInflater) pContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			switch (type) {
			case ERROR:

				break;
			case WARN:

				break;
			case INFO:

				break;
			case SUCCESS:

				break;
			default:
				break;
			}
			
			Toast toast = new Toast(pContext);
			toast.setText(content);
			toast.setDuration(Toast.LENGTH_SHORT);
			
			return toast;
		}

		public Builder setTitle(CharSequence pTitle) {
			this.title = pTitle;
			return this;
		}

		public Builder setContent(CharSequence pContent) {
			this.content = pContent;
			return this;
		}

		public Builder setType(Type pType) {
			type = pType;
			return this;
		}
	}

	public static enum Type {
		ERROR, INFO, SUCCESS, WARN, DEFAULT
	}
}
