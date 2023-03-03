package au.coaas.cre.handlers.cst;

import csiro.perccom.csto.manager.CSTManager;

public class CSTManagerSingleton {

	private static volatile CSTManager singleton = null;

	private CSTManagerSingleton() {
	}

	public synchronized static CSTManager getInstance() {
		if (singleton == null) {
			synchronized (CSTManagerSingleton.class) {
				if (singleton == null) {
					singleton = new CSTManager();
				}
			}
		}

		return singleton;
	}
}
