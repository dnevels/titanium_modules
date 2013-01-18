/**
 * Ti.Box Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Please see the LICENSE included with this distribution for details.
 */

package ti.box.calls;


import java.io.IOException;
import java.util.HashMap;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import ti.box.Constants;
import ti.box.Util;

import com.box.androidlib.Box;
import com.box.androidlib.ResponseListeners.MoveListener;

public class Move {

	/**
	 * Prevents instantiation.
	 */
	private Move() {}
	
	public static void call(final KrollProxy proxy, HashMap args) {
		
		KrollDict argsDict = new KrollDict(args);
		int objectId = argsDict.getInt("objectId");
		Boolean isFolder = argsDict.getBoolean("isFolder");
		int destinationFolderId = argsDict.getInt("destinationFolderId");

	    final KrollFunction success = (KrollFunction)args.get("success");
    	final KrollFunction error = (KrollFunction)args.get("error");
		
	    final Box box = Box.getInstance(Constants.API_KEY);
	    box.move(Constants.getAuthToken(), isFolder?"folder":"file", objectId, destinationFolderId, new MoveListener() {

			@Override
			public void onIOException(IOException ex) {
				Util.handleIOException(proxy, ex, error);
			}
			
			@Override
			public void onComplete(String status) {
				if (status.equals("s_move_node")) {
					if (success != null)
						success.callAsync(proxy.getKrollObject(), new Object[] {});
				}
				else if (status.equals("e_move_node")) {
					Util.handleError(proxy, "An error happened while we were moving your file. Try again?", status, error);
				}
				else {
					Util.handleCommonStatuses(proxy, status, error);
				}
			}
		});
	}
}
