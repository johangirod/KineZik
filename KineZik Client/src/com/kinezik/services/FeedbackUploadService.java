package com.kinezik.services;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.kinezik.database.FeedbackDAO;
import com.kinezik.network.Message;

public class FeedbackUploadService extends IntentService {

	public FeedbackUploadService() {
		super("FeedbackUploadService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("TRACE", "FeedbackUploadService started");
		ArrayList<String> feedbacks;
		// Recuperer ici les feedback depuis un fichier
		FeedbackDAO feeDAO = new FeedbackDAO(getApplication());
		feedbacks = feeDAO.getFeedback();
		Iterator <String> iteFeedback = feedbacks.iterator();
		while (iteFeedback.hasNext()){
			Message message = new Message();
			message.add("JSON", iteFeedback.next());
			message.add("action", "sendFeedback");
			message.send();
			Log.d("TRACE", "Feedback envoyés au serveur");
		}
	}

}
