var ga = require('ti.ga'),
	trackingID = "UA-XXXX-1";

//Create our tracker
var tracker = ga.createTracker({
	ga.TRACKING_ID,"UA-XXXX-1"
});

//Set the GA app name to the Titanium Project Name
tracker.createValue(ga.APP_NAME,Ti.App.name);

//Set the GA app version to the Titanium Project Name
tracker.createValue(ga.APP_VERSION,Ti.App.version);

//Set the GA app id to the Titanium Project Name
tracker.createValue(ga.APP_ID,Ti.App.id);

//Turn on anonymize IP
tracker.createValue(ga.ANONYMIZE_IP,ga.HELPER_CONSTANT_TRUE);

//Turn off anonymize IP
tracker.createValue(ga.ANONYMIZE_IP,ga.HELPER_CONSTANT_FALSE);

//Turn on https
tracker.createValue(ga.USE_SECURE,ga.HELPER_CONSTANT_TRUE);

//Turn off https
tracker.createValue(ga.USE_SECURE,ga.HELPER_CONSTANT_FALSE);